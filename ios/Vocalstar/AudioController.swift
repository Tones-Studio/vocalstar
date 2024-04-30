//
//  AudioController.swift
//  AudioCallBackTemplate
//
//  Created by Mathias Dietrich info@tech41.de on 15.11.23.
// output select  - profiling
//
import AudioToolbox
import AVFoundation

@objc protocol AURenderCallbackDelegate {
    func performRender(_ ioActionFlags: UnsafeMutablePointer<AudioUnitRenderActionFlags>,
        inTimeStamp: UnsafePointer<AudioTimeStamp>,
        inBufNumber: UInt32,
        inNumberFrames: UInt32,
        ioData: UnsafeMutablePointer<AudioBufferList>) -> OSStatus
}

private let AudioController_RenderCallback: AURenderCallback = {(inRefCon,
        ioActionFlags/*: UnsafeMutablePointer<AudioUnitRenderActionFlags>*/,
        inTimeStamp/*: UnsafePointer<AudioTimeStamp>*/,
        inBufNumber/*: UInt32*/,
        inNumberFrames/*: UInt32*/,
        ioData/*: UnsafeMutablePointer<AudioBufferList>*/)
    -> OSStatus
in
    let delegate = unsafeBitCast(inRefCon, to: AURenderCallbackDelegate.self)
    let flags = UnsafeMutablePointer<AudioUnitRenderActionFlags>(bitPattern:1) // TODO fix this hack
    let result = delegate.performRender(flags!,
        inTimeStamp: inTimeStamp,
        inBufNumber: inBufNumber,
        inNumberFrames: inNumberFrames,
        ioData: ioData!)
    return result
}

@objc(AudioController)
class AudioController: NSObject {

    static let shared = AudioController()

    let audioFx = AudioFx.shared
    let sessionInstance = AVAudioSession.sharedInstance()

    var inputDeviceName : String = ""
    var latency = 0.0
    var inputLatency = 0.0
    var outputLatency = 0.0
    var sampleRate = 48000.0 // 48000
    var frames = 0
    var preferedFrames = 64
    var preferedSampleRate = 48000
    var isOnSpeaker = false
    var isHeadphonesConnected = false

    var inputDeviceId : String = ""
    var inputPortType: AVAudioSession.Port = .builtInMic
    var outputPortType: AVAudioSession.Port = .builtInSpeaker
    var outputDeviceName : String = ""
    var outputDeviceId : String = ""
    var inputs : [String] = []
    var outputs : [String] = []

    var isGettingDevices = false
    var isSetup = false

    private(set) var audioChainIsBeingReconstructed: Bool = false

    override init() {
        super.init()
    }
    
    func portTypeToInt(type:AVAudioSession.Port)->Int{
        switch(type){
 
        case .builtInMic:
            return 1
            
        case .headsetMic:
            return 2
            
        case .usbAudio:
            return 3
            
        default:
            return 0
        }
    }
    
    // Example of calling DSP code from the UI
    func setMicVolume(volume: Double){
        audioFx.setMicLevel(volume: volume)
    }
    
    func setSpeaker(isSpeaker:Bool){
        do{
            if(isSpeaker){
                try sessionInstance.overrideOutputAudioPort(AVAudioSession.PortOverride.speaker)
            }else{
                try sessionInstance.overrideOutputAudioPort(AVAudioSession.PortOverride.none)
            }
        }catch{
            print(error.localizedDescription)
        }
    }

    func isCurrentOutput(portType: AVAudioSession.Port) -> Bool {
        sessionInstance.currentRoute.outputs.contains(where: { $0.portType == portType })
    }
    
    func setOutputDevice(name:String){
        // TODO
    }
    
    func setInputDevice(name:String){
        guard let availableInputs = sessionInstance.availableInputs else {
            print("No inputs available ")
            return
        }
        
        for audioPort in availableInputs {
            if(audioPort.portName == name){
                setPreferredInput(port:audioPort)
            }
        }
    }
    
    func setPreferredInput(port: AVAudioSessionPortDescription) {
          do {
              try sessionInstance.setPreferredInput(port)
          } catch let error as NSError {
              print("audioSession error change to input: \(port.portName) with error: \(error.localizedDescription)")
          }
    }
    
    // handles interruption of AVAudioSession, for example an incoming phone call. After the call we are restarting the AVAudioSession
    @objc func handleInterruption(_ notification: Notification) {
        let theInterruptionType = (notification as NSNotification).userInfo![AVAudioSessionInterruptionTypeKey] as! UInt
        NSLog("Session interrupted > --- %@ ---\n", theInterruptionType == AVAudioSession.InterruptionType.began.rawValue ? "Begin Interruption" : "End Interruption")
        
        if theInterruptionType == AVAudioSession.InterruptionType.began.rawValue {
            audioFx.stop()
        }
        
        if theInterruptionType == AVAudioSession.InterruptionType.ended.rawValue {
            // make sure to activate the session
            do {
                try sessionInstance.setActive(true)
            } catch let error as NSError {
                NSLog("AVAudioSession set active failed with error: %@", error)
            } catch {
                fatalError()
            }
            audioFx.start(sampleRate: sampleRate, blocksize: frames, activeMicType: portTypeToInt(type: inputPortType))
        }
    }
    
    // Called when the user changes the Audio device, for example pluging in headphones
    @objc func handleRouteChange(_ notification: Notification) {
        isGettingDevices = true
        let _ = (notification as NSNotification).userInfo![AVAudioSessionRouteChangeReasonKey] as! UInt
        let routeDescription = (notification as NSNotification).userInfo![AVAudioSessionRouteChangePreviousRouteKey] as! AVAudioSessionRouteDescription?
        
        guard let userInfo = notification.userInfo,
            let reasonValue = userInfo[AVAudioSessionRouteChangeReasonKey] as? UInt,
            let _ = AVAudioSession.RouteChangeReason(rawValue: reasonValue) else {
                isGettingDevices = false
                return
        }
        
        // logging the change
        NSLog("Route change:")
        if let reason = AVAudioSession.RouteChangeReason(rawValue: reasonValue) {
            switch reason {
            case .newDeviceAvailable:
                NSLog("     NewDeviceAvailable")
                let session = AVAudioSession.sharedInstance()
                isHeadphonesConnected = hasHeadphones(in: session.currentRoute)
            case .oldDeviceUnavailable:
                NSLog("     OldDeviceUnavailable")
                if let previousRoute =
                    userInfo[AVAudioSessionRouteChangePreviousRouteKey] as? AVAudioSessionRouteDescription {
                    isHeadphonesConnected = hasHeadphones(in: previousRoute)
                }
            case .categoryChange:
                NSLog("     CategoryChange")
                NSLog(" New Category: %@", AVAudioSession.sharedInstance().category.rawValue)
            case .override:
                NSLog("     Override")
            case .wakeFromSleep:
                NSLog("     WakeFromSleep")
            case .noSuitableRouteForCategory:
                NSLog("     NoSuitableRouteForCategory")
            case .routeConfigurationChange:
                NSLog("     RouteConfigurationChange")
            case .unknown:
                NSLog("     Unknown")
            @unknown default:
                NSLog("     UnknownDefault(%zu)", reasonValue)
            }
        } else {
            NSLog("     ReasonUnknown(%zu)", reasonValue)
        }
        
        if let prevRout = routeDescription {
            NSLog("Previous route:\n")
            NSLog("%@", prevRout)
            NSLog("Current route:\n")
            NSLog("%@\n", AVAudioSession.sharedInstance().currentRoute)
        }
        isGettingDevices = false
        DispatchQueue.main.async {
            self.reset()
        }
    }
    
    // Under rare circumstances the system terminates and restarts its media services daemon.
    @objc func handleMediaServerReset(_ notification: Notification) {
        NSLog("Media server has reset")
        reset()
    }
    
    func hasHeadphones(in routeDescription: AVAudioSessionRouteDescription) -> Bool {
        // Filter the outputs to only those with a port type of headphones.
        return !routeDescription.outputs.filter({$0.portType == .headphones}).isEmpty
    }
    
    func getDevices(){
        if isGettingDevices {
            return
        }
        isGettingDevices = true
        inputs.removeAll()
        guard let availableInputs = AVAudioSession.sharedInstance().availableInputs else {
            //print("No inputs available ")
            return
        }
        
        for audioPort in availableInputs {
            inputs.append(audioPort.portName)
        }
        if(sessionInstance.currentRoute.inputs.first != nil){
            inputDeviceName =  sessionInstance.currentRoute.inputs.first!.portName
            inputDeviceId =  sessionInstance.currentRoute.inputs.first!.uid
            inputPortType = sessionInstance.currentRoute.inputs.first!.portType
           // print("Device Name: \(inputDeviceName)   DeviceID: \(inputDeviceId)")
        }
        
        outputs.removeAll()
        let availableOutputs =  sessionInstance.currentRoute.outputs
        for audioPort in availableOutputs {
           outputs.append(audioPort.portName)
        }
        if(AVAudioSession.sharedInstance().currentRoute.outputs.first != nil){
            outputDeviceName = sessionInstance.currentRoute.outputs.first!.portName
            outputDeviceId = sessionInstance.currentRoute.outputs.first!.uid
            outputPortType = sessionInstance.currentRoute.outputs.first!.portType
        }
        isGettingDevices = false
    }
    
    private func setupAudioSession() {
        do {
            // we are going to play and record so we pick that category
            do {
                if #available(iOS 10.0, *) {
                    try sessionInstance.setCategory(.playAndRecord,mode: .default,  options: [.mixWithOthers, .allowBluetoothA2DP])  //.allowBluetooth
                } else {
                    try sessionInstance.setCategory(.playAndRecord)
                }
            } catch let error as NSError {
                try XExceptionIfError(error, "couldn't set session's audio category")
            } catch {
                fatalError()
            }
            
            let duration = Double(preferedFrames) / Double(sampleRate)
            let bufferDuration: TimeInterval =  duration //1.0/ 1000.0 // Secconds
            do {
                try sessionInstance.setPreferredIOBufferDuration(bufferDuration)
            } catch let error as NSError {
                try XExceptionIfError(error, "couldn't set session's I/O buffer duration")
            } catch {
                fatalError()
            }
            
            do {
                // set the session's sample rate
                try sessionInstance.setPreferredSampleRate(Double(preferedSampleRate)) // Samples per second
            } catch let error as NSError {
                try XExceptionIfError(error, "couldn't set session's preferred sample rate")
            } catch {
                fatalError()
            }
            
            // add interruption handler
            NotificationCenter.default.addObserver(self,
                selector: #selector(self.handleInterruption(_:)),
                name: AVAudioSession.interruptionNotification,
                object: sessionInstance)
            
            // we don't do anything special in the route change notification
            NotificationCenter.default.addObserver(self,
                selector: #selector(self.handleRouteChange(_:)),
                name: AVAudioSession.routeChangeNotification,
                object: sessionInstance)
            
            // if media services are reset, we need to rebuild our audio chain
            NotificationCenter.default.addObserver(self,
                selector: #selector(self.handleMediaServerReset(_:)),
                name: AVAudioSession.mediaServicesWereResetNotification,
                object: sessionInstance)
            
            do {
                // activate the audio session
                try sessionInstance.setActive(true)
                inputLatency = sessionInstance.inputLatency
                outputLatency = sessionInstance.outputLatency
                latency = inputLatency + outputLatency
                sampleRate = sessionInstance.sampleRate
                frames = Int(sessionInstance.ioBufferDuration * sampleRate)
                inputPortType = sessionInstance.currentRoute.inputs.first!.portType
                outputPortType = sessionInstance.currentRoute.outputs.first!.portType
            } catch let error as NSError {
                try XExceptionIfError(error, "couldn't set session active")
            } catch {
                fatalError()
            }
        } catch let e as CAXException {
            NSLog("Error returned from setupAudioSession: %d: %@", Int32(e.mError), e.mOperation)
        } catch _ {
            NSLog("Unknown error returned from setupAudioSession")
        }
    }
    
    var sessionSampleRate: Double {
        return sessionInstance.sampleRate
    }
    
    public func setup() {
        if (isSetup){ // allowed to call only once from extern - call reset after that
            return
        }
        isSetup = true
        self.setupAudioSession()
        audioFx.start(sampleRate: sampleRate, blocksize: frames, activeMicType: portTypeToInt(type: inputPortType))
    }
    
    public func reset(){
        if audioChainIsBeingReconstructed {
            return
        }
        audioChainIsBeingReconstructed = true
        isSetup = false
        self.setup()
        audioChainIsBeingReconstructed = false
    }
}

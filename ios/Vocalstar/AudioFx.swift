//
//  AudioFx.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 18.11.23.
//

import Foundation
import AudioToolbox
import AVFoundation
import SwiftUI
import DSP

class AudioFx{

    var dsp = DSP()
    static let shared = AudioFx()
    var ringbufferL : RingBuffer<Float32>?
    var ringbufferR : RingBuffer<Float32>?
    
    var blocksize = 0
    
    func setMicLevel(volume: Double){
        dsp.setMicLevel(volume)
    }
    
    func stop(){
        avengine.stop()
    }
    
    func writeToBuffer(bufferL : UnsafeMutablePointer<Float32>, bufferR : UnsafeMutablePointer<Float32>, blocksize :Int){
        var i = 0
        while i < blocksize{
            let tL = ringbufferL!.write(bufferL[i] * 0.8)
            let tR = ringbufferR!.write(bufferR[i] * 0.8)
            if !tL || !tR{
                print("could not write into Ringbuffer")
                return
            }
            i = i + 1
        }
    }
    
    private let avengine = AVAudioEngine()
    var sampleRate = 0.0
    
    let reverb =  AVAudioUnitReverb()
    let delay =  AVAudioUnitDelay()
    let delay2 =  AVAudioUnitDelay()
    let eq = AVAudioUnitEQ()
    
    let sink = AVAudioSinkNode() { (timeStamp, frames, audioBufferList) ->
        OSStatus in

        let ptr = audioBufferList.pointee.mBuffers.mData?.assumingMemoryBound(to: Float.self)
       
        // Redner dsp
        AudioFx.shared.dsp.render(ptr, Int32(frames))
        
        var samples = [Float]()
        samples.append(contentsOf: UnsafeBufferPointer(start: ptr, count: Int(frames)))
        
        for frame in 0..<frames {
            let ok = AudioFx.shared.ringbufferL?.write(samples[Int(frame)])
            let ok2 = AudioFx.shared.ringbufferR?.write(samples[Int(frame)])
        }
        return noErr
    }

    let source = AVAudioSourceNode() { (silence, timeStamp, frameCount, audioBufferList) ->
        OSStatus in
        let ablPointer = UnsafeMutableAudioBufferListPointer(audioBufferList)
        for frame in 0..<Int(frameCount) {
            let valueL = shared.ringbufferL!.read()
            let valueR = shared.ringbufferR!.read()
            let bufL: UnsafeMutableBufferPointer<Float> = UnsafeMutableBufferPointer(ablPointer[0])
            let bufR: UnsafeMutableBufferPointer<Float> = UnsafeMutableBufferPointer(ablPointer[1])
            if valueL != nil{
                bufL[frame] = valueL!
            }
            else{
                bufL[frame] = 0
            }
            if valueR != nil{
                bufR[frame] = valueR!
            }
            else{
                bufR[frame] = 0
            }
        }
        return noErr
    }
    
    func idFromMicUid(uid:String) ->Int{
        switch(uid){
        case "Wired Microphone":
            return 1
            
        case "Built-In Microphone":
        return 2
            
        case "USB":
        return 3
            
        default:
            return 0
        }
    }
    
    func start(sampleRate:Double, blocksize : Int, activeMicType:Int ){
        if avengine.isRunning{
            avengine.stop()
            dsp.stop()
        }
        
        self.sampleRate = sampleRate
        self.blocksize = blocksize
        ringbufferL = RingBuffer<Float>(count: Int(blocksize * 2))
        ringbufferR = RingBuffer<Float>(count: Int(blocksize * 2))

        do{
            let stereoFormat = AVAudioFormat(standardFormatWithSampleRate: sampleRate, channels: 2)
            let inputNode = avengine.inputNode
            let mainMixer = avengine.mainMixerNode
            let outputNode = avengine.outputNode
          
            // Attach
            avengine.attach(source)
            avengine.attach(sink)
            avengine.attach(eq)
            avengine.attach(delay)
            avengine.attach(delay2) // https://gmcerveny.medium.com/splitting-signals-in-music-apps-with-avaudioengine-53e144691167
            avengine.attach(reverb)
            
            avengine.connect(inputNode, to:eq, format: stereoFormat)
            avengine.connect(eq, to:sink, format: stereoFormat)
            
            avengine.connect(source, to:delay, format: stereoFormat)
            avengine.connect(delay, to:reverb, format: stereoFormat)
            avengine.connect(reverb, to:mainMixer, format: stereoFormat)
            avengine.connect(mainMixer, to:outputNode, format: stereoFormat)
            
            // Source
            source.volume = 1.0
            
            // EQ
            let FREQUENCY: [Float] = [31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000]
            let VALUES: [Float] = [0, 0.5, 0.7, 1, 1, 1, 1, 1.2, 1.5, 1]
            for i in 0...9 {
                self.eq.bands[i].filterType = .parametric
                self.eq.bands[i].frequency = FREQUENCY[i]
                self.eq.bands[i].bandwidth = 0.5 // half an octave
                self.eq.bands[i].gain = VALUES[i]
                self.eq.bands[i].bypass = true
            }
            eq.globalGain = 8
            eq.bypass = false
            
            // Delay
            delay.reset()
            delay.delayTime = 0.24
            delay.feedback = 5
            delay.wetDryMix = 1
            delay.bypass = false
            
            // Delay2 - pre delays reverb
            delay2.reset()
            delay2.delayTime = 0.24
            delay2.feedback = 0
            delay2.wetDryMix = 100
            delay2.bypass = false
            
            // Reverb
            reverb.loadFactoryPreset(AVAudioUnitReverbPreset.largeHall)
            reverb.wetDryMix = 8 // 15
            reverb.bypass = false
            
            //Start
            dsp.setup(sampleRate, Int32(blocksize), Int32(activeMicType))
            try avengine.start()
        }catch{
            print(error.localizedDescription)
        }
    }
}

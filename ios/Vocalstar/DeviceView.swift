//
//  DeviceView.swift
//  VocalstarFramework
//
//  Created by Mathias Dietrich on 13.07.23.
//

import SwiftUI
import AVFoundation

struct DeviceView: View {
    @State var inputs : [String] = []
    @State var outputs : [String] = []
    @State var input = ""
    @State var output = ""
    @State var isSpeaker = false
    @State var engine = Engine.shared
    
    let timer = Timer.publish(every: 0.2, on: .main, in: .common).autoconnect()
   
let text = """
Best:
- If you have an audio interface. great, use USB!
- Use the Lightning to USB Camera Adapter
- Connect a powered USB hub to power the interface
- Use a high quality condenser mic

Good:
- Use wired headphones like Apple Earpods or similar
- Select the iPhone mic for a full sound
- Select the headphone mic for convinience

Varying:
- Bluetooth is only good for listening
- Bluetooth will introduce latencies for recording
"""
    func setSpeaker(isSpeaker:Bool){
        do{
            if(isSpeaker){
                engine.isOnSpeaker = true
                engine.isSeeking = true
                try AVAudioSession.sharedInstance().overrideOutputAudioPort(AVAudioSession.PortOverride.speaker)
            }else{
                engine.isSeeking = false
                engine.isOnSpeaker = false
                try AVAudioSession.sharedInstance().overrideOutputAudioPort(AVAudioSession.PortOverride.none)
            }
            AudioController.shared.getDevices()
        }catch{
            
        }
    }
    
    func setInputDevice(name:String){
        engine.setInputDevice(name: name)
    }

    var body: some View {
        ScrollView{
            VStack{
                Group{
                    Text("In & Out").foregroundColor(Color("PageTitle")).font(Font.custom(FONT, size: 35))
                    Divider()
                    Text("Input Devices").foregroundColor(.blue).font(Font.custom(FONT, size: 25))
                    Picker("Mic", selection: $input) {
                        if(inputs.count > 0){
                            ForEach(inputs, id: \.self) { status in
                                Text(status).foregroundColor(.white).font(Font.custom(FONT, size: 30))
                            }.onChange(of: input) { _name in
                                setInputDevice(name:_name)
                            }
                        }else{
                            Text("No MIC - plug in Earbuds or USB AudioInterface").foregroundColor(.white).font(Font.custom(FONT, size: 30))
                        }
                    }
                    .pickerStyle(.segmented).foregroundColor(.white).font(Font.custom(FONT, size: 30))
                    Text("Output Devices").foregroundColor(.blue).font(Font.custom(FONT, size: 25))
                    Picker("Ouput", selection: $output) {
                        if(inputs.count > 0){
                            ForEach(outputs, id: \.self) { status in
                                Text(status).foregroundColor(.white).font(Font.custom(FONT, size: 30))
                            }.onChange(of: output) { _name in
    
                            }
                        }
                    }.pickerStyle(.segmented).foregroundColor(.white).font(Font.custom(FONT, size: 30))
                    Toggle("Speaker", isOn: $isSpeaker).padding(.trailing, 5).onChange(of: isSpeaker) { _isOn in
                        setSpeaker(isSpeaker: isSpeaker)
                    }
                }
                
                Divider()
               
                HStack{
                    Slider(
                        value: $engine.micVolume,
                        in: 0...1,
                        onEditingChanged: { editing in
                        }
                    )
                    Image(systemName: "music.mic")
                }.frame(height:20)
                Divider()
                Text(text).font(Font.custom(FONT, size: 15)).foregroundColor(.gray).multilineTextAlignment(.leading)
            }.onReceive(timer) { someInput in
                AudioController.shared.getDevices()
                inputs.removeAll()
                for item in AudioController.shared.inputs{
                    inputs.append(item)
                }
                outputs.removeAll()
                for item in AudioController.shared.outputs{
                    outputs.append(item)
                }
                output = AudioController.shared.outputDeviceName
                input = AudioController.shared.inputDeviceName
            }
        }
    }//Body
} // Struct

struct DeviceView_Previews: PreviewProvider {
    static var previews: some View {
        DeviceView()
    }
}

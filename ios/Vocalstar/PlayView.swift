//
//  PlayView.swift
//  VocalstarFramework
//
//  Created by Mathias Dietrich on 13.07.23.
//

import Foundation
import AVFoundation
import CoreData
import UIKit
import SwiftUI
import MediaPlayer
import StoreKit
import CoreMedia
import CoreAudio
import AudioToolbox

struct ImageButtonStyle: ButtonStyle {
    var image: String
    var pressedImage: String
    func makeBody(configuration: Self.Configuration) -> some View {
      let displayImage = configuration.isPressed ? pressedImage : image
      return Image(displayImage, bundle: Bundle(for: Engine.self))
    }
}

struct ImageButton: View {
    var image: String
    var pressedImage: String
    var action: () -> Void
    var body: some View {
        Button(action: self.action){}
            .buttonStyle(ImageButtonStyle(image: image, pressedImage: pressedImage))
    }
}

struct VolumeView: UIViewRepresentable {
    func makeUIView(context: Context) -> MPVolumeView {
        MPVolumeView(frame: .zero)
    }

    func updateUIView(_ view: MPVolumeView, context: Context) {

    }
}

struct PlayView: View {
    enum DisplayMode: Int {
            case system = 0
            case dark = 1
            case light = 2
    }
    
    @StateObject var engine = Engine.shared
    @AppStorage("displayMode") var displayMode: DisplayMode = .dark
    @State var requestAgain = false
    @State var toogle = false
    @State var startTime = "00:00"
    @State var endTime = "00:00"
    @State var songPosPercent = 0.0
    @State var artist = "Vocalstar"
    @State var title = "Vocalstar"
    @State var songImage = Image("Vocalstar_Splash")
    @State var isEditing = false
    @State var isRadio = false
    let timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()

    func playtoggle() {
        engine.togglePlay()
    }
    
    func back(){
        engine.back()
    }
    
    func forward(){
        engine.forward()
    }
    
    func requestPermission(){
         MPMediaLibrary.requestAuthorization {
             authorizationStatus in
             switch(authorizationStatus){
             case .authorized:
                 requestAgain = false
                 break
                 
             case .denied:
                 requestAgain = true
                 return
                 
             case .restricted:
                 requestAgain = true
                 return
             default:
                 break
             }
         }
   
        AVAudioSession.sharedInstance().requestRecordPermission { granted in
            if granted {
                print("granted")
                requestAgain = false
            } else {
                requestAgain = true
                return
            }
        }
    }
  
    var body: some View {
        let bounds = UIScreen.main.bounds
        let width = bounds.size.width 
        let height = bounds.size.height
        if(requestAgain){
            VStack{
                Text("Permissions").font(Font.custom(FONT, size: 35)).foregroundColor(.white)
                Divider()
                Text("To make Vocalstar work please go into the Settings App, scroll down to Vocalstar and enable Microphone").font(Font.custom(FONT, size: 17)).foregroundColor(.red)
                Button{
                   requestPermission()
                }
            label: {
                Label("Check again", systemImage: "circle.and.line.horizontal").font(Font.custom(FONT, size: 30))
                    .foregroundColor(.orange)
                    .frame(alignment: .trailing)
                    .cornerRadius(2)
                    .padding(2)
                }
            }
        }else{
            GeometryReader{ geo in
                VStack{
                    
                    // Song artist, title and Image
                    Spacer().frame(height:20)
                    if height < 668{
                        Text(artist).foregroundColor(Color("SongTitle")).font(.custom(FONT_B, size: 25))
                    }else{
                        Text(artist).foregroundColor(Color("SongTitle")).font(.custom(FONT_B, size: 30))
                    }
                    let imageWidth = width * 3/4
                    songImage.resizable().frame(width:imageWidth,height:imageWidth).aspectRatio(contentMode: .fill)
                    if height < 668{
                        Text(title).foregroundColor(Color("SongTitle")).font(.custom(FONT_B, size: 30))
                    }else{
                        Text(title).foregroundColor(Color("SongTitle")).font(.custom(FONT_B, size: 36))
                    }

                    // Timeline
                    if isRadio{
                        Text("Radio")
                    }else{
                        HStack{
                            GeometryReader{ proxy in
                                ZStack{
                                    Text("\(startTime)").foregroundColor(Color(red:0.3,green:0.3,blue:0.3))
                                        .frame(width: proxy.size.width,alignment:.leading)
                                    MusicProgressSlider(value: $songPosPercent,
                                                        inRange: 0...100,
                                                        activeFillColor:.orange,
                                                        fillColor:.orange,
                                                        emptyColor:.gray,
                                                        height:15,
                                                        onEditingChanged: { editing in
                                        if !editing {
                                            engine.scrollDone()
                                        }
                                        isEditing = editing
                                    }
                                    )
                                    .tint(Color.green)
                                    .accentColor(Color.yellow)
                                    .offset(x:0,y:25).frame(width:proxy.size.width - 50, height:7)
                                    
                                    Text("\(endTime)").foregroundColor(Color(red:0.3,green:0.3,blue:0.3)).padding(.trailing, 2)
                                        .frame(width: proxy.size.width,alignment:.trailing)
                                    
                                }.frame(width:proxy.size.width, height:15)
                            }
                        }
                    }
                    
                    // Transport
                    if height < 668{
                        Spacer().frame(height:35)
                    }else{
                        Spacer().frame(height:45)
                    }
                    HStack{
                        ImageButton(
                            image: "backwards",
                            pressedImage: "backwards_down",
                            action: { back() }
                        ).frame(width:35, height:35)
                        
                        Spacer().frame(width:50, height:0)
                        ImageButton(
                            image: engine.isPlaying ? "pause" : "play",
                            pressedImage: engine.isPlaying ? "pause_down" : "play_down",
                            action: {playtoggle() }
                        ).frame(width:35, height:35)
                        
                        Spacer().frame(width:50, height:0)
                        ImageButton(
                            image: "forward",
                            pressedImage: "forward_down",
                            action: {forward() }
                        ).frame(width:35, height:35).padding()
                    }
                    
                    if height > 668{ // NOT SSE
                        Spacer().frame(height:40)
                    }
                    
#if targetEnvironment(simulator)
                    Slider(
                        value: $engine.micVolume,
                        in: 0...1,
                        onEditingChanged: { editing in
                        }
                    )
#else
                    VolumeView().frame( height:20).padding(.horizontal)
#endif
                    // MIC Slider
                    Spacer().frame(height:20)
                    HStack{
                        Slider(
                            value: $engine.micVolume,
                            in: 0...1,
                            onEditingChanged: { editing in
                            }
                        )
                        Image(systemName: "music.mic")
                    }.frame(width:geo.size.width - 25, height:20)
                }.frame(maxWidth: .infinity, maxHeight: geo.size.height - 85, alignment:.center).environmentObject(engine).onAppear(){
                    AVAudioSession.sharedInstance().requestRecordPermission { granted in
                        // The user grants access. Present recording interface.
                        if granted {
                            print("granted")
                        } else {
                            requestAgain = true
                            return
                        }
                    }
                    engine.updateView()
                }.onAppear(){
                    engine.updatePlayer()
                }.onReceive(timer) { input in
                    engine.updatePlayer()
                    if isEditing{
                        engine.scroll(percent: songPosPercent)
                        print(songPosPercent)
                        startTime = engine.getStartTime()
                        endTime = engine.getEndTime()
                    }else{
                        startTime = engine.getStartTime()
                        endTime = engine.getEndTime()
                        songPosPercent = engine.musicPlayer.songPosPercent
                        title = engine.musicPlayer.title
                        artist = engine.musicPlayer.artist
                        songImage = engine.musicPlayer.songImage
                        isRadio = engine.musicPlayer.isRadio
                    }
                
                }// geometry
            } //if
        } // Body
    }
} // Struct

struct PlayView_Previews: PreviewProvider {
    static var previews: some View {
        PlayView()
    }
}

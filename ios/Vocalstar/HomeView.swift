//
//  HomeView.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 12.07.23.
//

import SwiftUI

struct CustomButtonStyle: ButtonStyle {
    
    var onPressed: () -> Void
    
    var onReleased: () -> Void
    
    // Wrapper for isPressed where we can run custom logic via didSet (or willSet)
    @State private var isPressedWrapper: Bool = false {
        didSet {
            // new value is pressed, old value is not pressed -> switching to pressed state
            if (isPressedWrapper && !oldValue) {
                onPressed()
            }
            // new value is not pressed, old value is pressed -> switching to unpressed state
            else if (oldValue && !isPressedWrapper) {
                onReleased()
            }
        }
    }
    
    // return the label unaltered, but add a hook to watch changes in configuration.isPressed
    func makeBody(configuration: Self.Configuration) -> some View {
        return configuration.label
            .onChange(of: configuration.isPressed, perform: { newValue in isPressedWrapper = newValue })
    }
}

struct HomeView: View {
    @Environment(\.scenePhase) var scenePhase
    let appDelegate = UIApplication.shared.delegate as! VocalstarApp
    @StateObject var engine = Engine.shared
    @State var isShowingSetting = false
   // @State private var showingPopoverSpotify = false
    @State var controller = AudioController.shared
    @State var headerHeight = 35
    
    /*
    var appRemote: SPTAppRemote? {
        get {
            return appDelegate.appRemote
        }
    }
     */
    
    func selectIos(){
        engine.stopPlaying()
        engine.player = .IOS
    }
    
    var body: some View {
        GeometryReader{ geometry in
                NavigationStack{
                    TabView {
                        PlayView()
                            .frame(maxWidth:.infinity, maxHeight: .infinity)
                            .tabItem {
                                Label("Sing",systemImage:"beats.headphones")
                            }
                        
                        DeviceView().padding()
                            .frame(maxWidth:.infinity, maxHeight: .infinity)
                            .tabItem {
                                Label("In & Out", systemImage: "music.mic")
                            }
                        
                        AboutView().padding()
                            .frame(maxWidth:.infinity, maxHeight: .infinity)
                            .tabItem {
                                Label("About", systemImage: "person.crop.circle.fill")
                            }
                    }.toolbar{ // Left IoI Button
                        ToolbarItem(placement: .topBarLeading) {
                            if engine.player == .IOS{
                                Link(destination: URL(string: engine.getMediaLink())!,label:{
                                    Image("Apple_Music_icon").resizable().frame(width:35,height:35).cornerRadius(10.0)
                                    Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                })
                            }else{
                                Button(action: selectIos, label: {
                                    Image("Apple_Music_icon").resizable().frame(width:35,height:35).cornerRadius(10.0)
                                })
                            }
                        }
                        
                        ToolbarItem(placement: .principal) {
                            Image("logo_intern").resizable().frame(width:100,height:19)
                        }
                        
                        // Right Spotify Button
                        /*
                        ToolbarItem(placement: .topBarTrailing) {
                            if engine.player == .SPOTIFY{
                                if !appDelegate.appRemote.isConnected{
                                    Button {
                                        appDelegate.appRemote.authorizeAndPlayURI("spotify:")
                                        engine.stopPlaying()
                                    } label: {
                                        Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                        Image("Spotify").resizable().frame(width:35,height:35).cornerRadius(10.0)
                                    }
                                }else{
                                    Link(destination: URL(string: engine.getMediaLinkspotify())!,label:{
                                        Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                        Image("Spotify").resizable().frame(width:39,height:39).cornerRadius(10.0)
                                    }).disabled(!engine.isSpotifyInstalled)
                                }
                            }else{
                                Button {
                                    if !engine.isSpotifyInstalled {
                                        showingPopoverSpotify = true
                                        return
                                    }
                                    engine.player = .SPOTIFY
                                    engine.stopPlaying()
                                    if !appDelegate.appRemote.isConnected{
                                        appDelegate.appRemote.authorizeAndPlayURI("spotify:")
                                    }
                                    //engine.stopPlaying()
                                    engine.updatePlayer()
                                } label: {
                                    Image("Spotify").resizable().frame(width:35,height:35).cornerRadius(10.0)
                                }
                                .alert("Please install Spotify", isPresented: $showingPopoverSpotify) {
                                    Button("OK", role: .cancel) { }
                                }
                            }
                        }*/
                    }.onAppear(){
#if targetEnvironment(simulator)
                        
#else
                        controller.preferedSampleRate = 48000
                        controller.preferedFrames = 64
                        controller.setup()
 #endif
                    }
            }
            .environment(\.colorScheme, .dark)
            .environmentObject(engine) //HStack
        }
    } // Body
} // Struct
 
struct HomeViewPreviews: PreviewProvider {
    static var previews: some View {
        
        HomeView()
            .previewDevice(PreviewDevice(rawValue: "iPhone 13"))
            .previewDisplayName("iPhone 13")
        
        HomeView()
            .previewDevice(PreviewDevice(rawValue: "iPhone 14"))
            .previewDisplayName("iPhone 14")

        HomeView()
            .previewDevice(PreviewDevice(rawValue: "iPhone 15 Pro Max"))
            .previewDisplayName("iPhone 15 Pro Max")
        
        HomeView()
            .previewDevice(PreviewDevice(rawValue: "iPhone SE 3rd generation"))
            .previewDisplayName("iPhone SE 3rd generation")
    }
}

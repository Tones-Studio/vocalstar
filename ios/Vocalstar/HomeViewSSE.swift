//
//  HomeView.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 12.07.23.
//

import SwiftUI

struct HomeViewSSE: View {
    @Environment(\.scenePhase) var scenePhase
    @StateObject var engine = Engine.shared
    @State var isShowingSetting = false
    @State var controller = AudioController.shared
    @State var desiredHeight = 0.0

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
                                    Image("Apple_Music_icon").resizable().frame(width:27,height:27).cornerRadius(10.0)
                                    Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                })
                            }else{
                                Button(action: selectIos, label: {
                                    Image("Apple_Music_icon").resizable().frame(width:27,height:27).cornerRadius(10.0)
                                })
                            }
                        }
                        
                        ToolbarItem(placement: .principal) {
                            Image("logo_intern").resizable().frame(width:100,height:17)
                        }
                        
                        /*
                        // Right Spotify Button
                        ToolbarItem(placement: .topBarTrailing) {
                            if engine.player == .SPOTIFY{
                                if !appDelegate.appRemote.isConnected{
                                    Button {
                                        appDelegate.appRemote.authorizeAndPlayURI("spotify:")
                                        engine.stopPlaying()
                                    } label: {
                                        Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                        Image("Spotify").resizable().frame(width:27,height:27).cornerRadius(10.0)
                                    }
                                }else{
                                    Link(destination: URL(string: engine.getMediaLinkspotify())!,label:{
                                        Image(systemName: "moonphase.full.moon").resizable().frame(width:7,height:7)
                                        Image("Spotify").resizable().frame(width:27,height:27).cornerRadius(10.0)
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
                                    Image("Spotify").resizable().frame(width:30,height:30).cornerRadius(10.0)
                                }
                                .alert("Please install Spotify", isPresented: $showingPopoverSpotify) {
                                    Button("OK", role: .cancel) { }
                                }
                            }
                        }
                         */
                    }.onAppear(){
                        if let window = UIApplication.shared.windows.first{
                                let phoneSafeAreaTopnInset = window.safeAreaInsets.top
                                desiredHeight = phoneSafeAreaTopnInset
                                print(desiredHeight)
                        }
#if targetEnvironment(simulator)
                        
#else
                        controller.preferedSampleRate = 48000
                        controller.preferedFrames = 64
                        controller.setup()
 #endif
                    }
            }//navstack
            .environment(\.colorScheme, .dark)
            .environmentObject(engine)
        }//geometry
    } // Body
} // Struct
 
struct HomeViewSSEPreviews: PreviewProvider {
    static var previews: some View {
        
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 13"))
            .previewDisplayName("iPhone 13")
        
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 14"))
            .previewDisplayName("iPhone 14")

        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 15 Pro Max"))
            .previewDisplayName("iPhone 15 Pro Max")
        
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone SE 3rd generation"))
            .previewDisplayName("iPhone SE 3rd generation")
    }
}

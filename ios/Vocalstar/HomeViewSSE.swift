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
                                Label("Sing",systemImage:"music.mic")
                            }
                        
                        DeviceView().padding()
                            .frame(maxWidth:.infinity, maxHeight: .infinity)
                            .tabItem {
                                Label("In & Out", systemImage: "speaker")
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
                    }.onAppear(){
#if targetEnvironment(simulator)
                 print("simulator - not starting audio engine")
#else
                        controller.preferedSampleRate = 48000
                        controller.preferedFrames = 64
                        controller.setup()
 #endif
                    }
            }//navstack
            .environment(\.colorScheme, .dark)
            .environmentObject(engine)
            .background(.black)
        }//geometry
    } // Body
} // Struct
 
struct HomeViewSSEPreviews: PreviewProvider {
    static var previews: some View {
        /*
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 13"))
            .previewDisplayName("iPhone 13")
        
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 14"))
            .previewDisplayName("iPhone 14")

        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone 15 Pro Max"))
            .previewDisplayName("iPhone 15 Pro Max")
        */
        HomeViewSSE()
            .previewDevice(PreviewDevice(rawValue: "iPhone SE 3rd generation"))
            .previewDisplayName("iPhone SE 3rd generation")
    }
}

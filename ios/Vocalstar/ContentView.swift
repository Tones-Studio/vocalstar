//
//  ContentView.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 17.11.23.
//

import SwiftUI
import DSP

struct ContentView: View {

    var body: some View {
        let bounds = UIScreen.main.bounds
        let height = bounds.size.height
        ZStack{
            if height < 668{
                HomeViewSSE().frame(maxWidth:.infinity, maxHeight:.infinity).padding().opacity(1.0)
            }else{
                HomeView().frame(maxWidth:.infinity, maxHeight:.infinity).padding().opacity(1.0)
            }
        }
    }
}

#Preview {
    ContentView()
}

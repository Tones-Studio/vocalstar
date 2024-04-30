//
//  RegisterView.swift
//  VocalstarFramework
//
//  Created by Mathias Dietrich on 13.07.23.
//

import SwiftUI

struct AboutView: View {
    
    @State var email: String = ""
    @State var firstname: String = ""
    @State var surname: String = ""
    @State var response: String = ""
    @State var responseColorIsRed = false
    
    let text =
"""
Would you like to be notified when our pro version is coming to the AppStore?

We call it Tones Studio! There will be many additional features for recording, editing, mixing and sharing.

For infos, FAQ and more please visit our website at:
"""
    
    let textContact =
"""
For comments or questions please contact us at:
"""
    
    let textCompany =
"""
Copyright:

TECH41 GmbH
Schützenstr. 110
22761 Hamburg
Germany

"""
    
    func dismissKeyboard() {
        //UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.endEditing(true) // 4
    }
    
    var body: some View {
        ZStack{
            VideoBgView().frame(maxWidth: .infinity, maxHeight:.infinity).opacity(0.3)
            ScrollView{
                VStack{
                    Spacer().frame(height:30)
                    Text("About").foregroundColor(Color("PageTitle")).font(Font.custom(FONT, size: 35))
                    Spacer()
                    Divider()
                    Text(text).font(.system(size: 16)).foregroundColor(.gray)
                    
                    Link("https://tones.studio", destination: URL(string: "https://tones.studio")!)
                    Divider()
                    Text(textContact).font(.system(size: 16)).foregroundColor(.gray)
                    Link("info@tones.studio", destination: URL(string: "mailto:info@tones.studio")!)
                    Spacer()
                    Divider()
                    Text(textCompany).font(.system(size: 16)).foregroundColor(.gray)
                    Link("https://tech41.de", destination: URL(string: "https://tech41.de")!)
                }.frame(maxWidth: .infinity, maxHeight: .infinity)
            }.frame(maxWidth: .infinity, maxHeight: .infinity)
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
    } // body
} // struct

struct AboutView_Previews: PreviewProvider {
    static var previews: some View {
        AboutView()
    }
}

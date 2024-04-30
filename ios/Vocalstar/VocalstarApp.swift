//
//  VocalstarApp.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 11.07.23.
//
import SwiftUI
import UIKit
import os.log
import CoreData

@main
class VocalstarApp : UIResponder, UIApplicationDelegate{

    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let window = UIWindow()
        self.window = window
        
        window.rootViewController = UIHostingController(rootView: ContentView().frame(maxWidth:.infinity, maxHeight:.infinity).ignoresSafeArea().background(.black)
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { (_) in
                Engine.shared.stopPlaying()
          }
          // 2
          .onReceive(NotificationCenter.default.publisher(for: UIApplication.willResignActiveNotification)) { (_) in

          })
        window.makeKeyAndVisible()
        return true
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
      return true
    }
    
    func enterForeground(){
        Engine.shared.stopPlaying()
    }
    
    func sceneWillEnterForeground(_scene: UIScene){
        enterForeground()
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        enterForeground()
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {

    }

    private func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: NSDictionary?) -> Bool {
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {

    }
}

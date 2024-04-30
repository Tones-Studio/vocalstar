//
//  IosPlayer.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 17.11.23.
//

import Foundation
import UIKit
import MediaPlayer
import SwiftUI

class IosPlayer : MusicPlayer, ObservableObject{
    
    @Published var startTime: String = "00:00"
    @Published var endTime: String = "00:00"
    @Published var songPosPercent: Double = 0.0
    @Published var title: String = "Vocalstar"
    @Published var artist: String = "Artist"
    @Published var songImage: Image = Image("Vocalstar_Splash")
    @Published var isRadio = false
    
    var _duration = 0.0
    
    func updateView(){
        objectWillChange.send()
    }
    
    let musicPlayerSystem = MPMusicPlayerApplicationController.systemMusicPlayer
    
    var duration:Double{
        get{
            return _duration
        }
    }
    
    func setTimeline(){
        if(musicPlayerSystem.nowPlayingItem != nil){
            _duration = musicPlayerSystem.nowPlayingItem!.playbackDuration
            let songPosSeconds = musicPlayerSystem.currentPlaybackTime
            if !songPosSeconds.isNaN {
                let timeLeft = _duration - songPosSeconds
                startTime = songPosSeconds.stringFromTimeInterval()
                endTime = timeLeft.stringFromTimeInterval()
                if(_duration > 0){
                    songPosPercent = songPosSeconds * 100.0 / _duration
                }else{
                    songPosPercent =  0
                }
                isRadio = false
            }
            else{
                isRadio = true
                startTime = "unknown"
                endTime = ""
            }
        }
    }
    
    func getPosition() -> Double {
        return 0
    }
    
    func isPlaying() -> Bool {
        return musicPlayerSystem.playbackState == .playing
    }
    
    func play(){
        musicPlayerSystem.play()
    }
    
    func stop(){
        musicPlayerSystem.stop()
    }

    func backwards() {
        if songPosPercent == 0{
            musicPlayerSystem.skipToPreviousItem()
        }else{
            musicPlayerSystem.skipToBeginning()
        }
        updateUIParams()
    }
    
    func forwards() {
        musicPlayerSystem.skipToNextItem()
        updateUIParams()
    }
    
    func setPosition(position: Double) {
        musicPlayerSystem.currentPlaybackTime = position
    }
    
    func updateUIParams(){
        if musicPlayerSystem.nowPlayingItem == nil{
            return
        }
        if(musicPlayerSystem.nowPlayingItem!.title != nil)
        {
            title = musicPlayerSystem.nowPlayingItem!.title!
        }else{
            title = ""
        }
        if(musicPlayerSystem.nowPlayingItem!.artist != nil)
        {
            artist = musicPlayerSystem.nowPlayingItem!.artist!
        }else{
            artist = ""
        }
        let img = musicPlayerSystem.nowPlayingItem!.artwork?.image(at: CGSize(width: 200,height:200))
        if(img != nil){
            songImage = Image(uiImage:img!)
        }else{
            Task{
                let img = musicPlayerSystem.nowPlayingItem!.artwork?.image(at: CGSize(width: 200,height:200))
                if img != nil{
                    songImage = Image(uiImage:img!)
                }else{
                    songImage = Image("Vocalstar_Splash")
                }
            }
        }
    }
}

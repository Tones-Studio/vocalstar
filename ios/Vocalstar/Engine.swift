//
//  Engine.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 22.11.23.
//

import Foundation
import AVFoundation
import CoreData
import SwiftUI
import MediaPlayer
import CoreMedia
import CoreAudio
import AudioToolbox

enum Screens{
    case HOME
    case ABOUT
}

enum PLAYER{
    case IOS
}

class Engine  : ObservableObject{
    
    static let shared = Engine()
 
    let iosPlayer =  IosPlayer()
    var newSongPosSeconds = 0.0
    
    @Published var player = PLAYER.IOS{
        didSet{
            musicPlayer = iosPlayer
            musicPlayer.updateUIParams()
        }
    }
    @Published var screen = Screens.HOME
    @Published var songPosSeconds = 0.0
    @Published var songDurationSeconds = 0.0
    @Published  var isPlaying = false
    @Published var lyrics = ""
    @Published var inputFormat : AVAudioFormat?
    @Published var outputFormat : AVAudioFormat?
    
    @Published var isOnSpeaker = false{
        didSet{
            AudioFx.shared.dsp.setMute(isOnSpeaker)
        }
    }
    @Published var isSeeking = false
    @Published var musicPlayer : MusicPlayer!
    @Published var micVolume : Double = 0.0{
        didSet {
            AudioFx.shared.dsp.setMicLevel(micVolume)
        }
    }
    
    init(){
        musicPlayer = iosPlayer
        isPlaying = musicPlayer.isPlaying()
        musicPlayer.updateUIParams()
    }
    
    func getStartTime() ->String{
        if isSeeking{
            return newSongPosSeconds.stringFromTimeInterval()
        }
        return musicPlayer.startTime
    }
    
    func getEndTime()->String{
        if isSeeking{
            return (musicPlayer.duration-newSongPosSeconds).stringFromTimeInterval()
        }
        return musicPlayer.endTime
    }
    
    // Timer
    let timer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { timer in
        if Engine.shared.isSeeking{
            return
        }
        Engine.shared.setTimeline()
    }
    

    
    func setTimeline(){
        musicPlayer.setTimeline()
        musicPlayer.updateView()
    }
    
    func getMediaLink() ->String{
        return "music://music.apple.com"
    }

    func scroll(percent: Double ){
        isSeeking = true
        newSongPosSeconds = percent * musicPlayer.duration / 100.0
    }
    
    func scrollDone(){
        isSeeking = false
        musicPlayer.setPosition(position: newSongPosSeconds)
    }
    
    func forward(){
        musicPlayer.forwards()
    }
    
    func back(){
        musicPlayer.backwards()
    }
    
    func updatePlayer(){
        musicPlayer.updateUIParams()
        musicPlayer.updateView()
    }
    
    func stopPlaying(){
        isPlaying = false
        musicPlayer.stop()
    }
    
    func updateView(){
        musicPlayer.updateView()
    }
    
    func togglePlay(){
        isPlaying = !isPlaying
        if (isPlaying){
            musicPlayer.play()
        }else{
            musicPlayer.stop()
        }
    }
    
    func setPreferredInput(port: AVAudioSessionPortDescription) {
          do {
              try AVAudioSession.sharedInstance().setPreferredInput(port)
          } catch let error as NSError {
              print("audioSession error change to input: \(port.portName) with error: \(error.localizedDescription)")
          }
    }
    
    func setInputDevice(name:String){
        guard let availableInputs = AVAudioSession.sharedInstance().availableInputs else {
            print("No inputs available ")
            return
        }

        for audioPort in availableInputs {
            if(audioPort.portName == name){
               setPreferredInput(port:audioPort)
            }
        }
    }
}


//
//  Protocols.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 17.11.23.
//

import Foundation
import SwiftUI

protocol MusicPlayer{
    var title :String { get set }
    var artist :String { get set }
    var songImage :Image { get set }
    var startTime : String{ get set }
    var endTime : String{ get set }
    var songPosPercent :Double{ get set }
    var duration:Double{ get  }
    var isRadio:Bool{ get  }
    
    func setTimeline();
    func play()
    func stop()
    func backwards()
    func forwards()
    func setPosition(position:Double)
    func getPosition()->Double
    func isPlaying()->Bool
    func updateUIParams()
    func updateView()
}


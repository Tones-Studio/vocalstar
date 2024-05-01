//
//  Extensions.swift
//  Vocalstar
//
//  Created by Mathias Dietrich on 22.11.23.
//

import Foundation

extension TimeInterval{
    func stringFromTimeInterval() -> String {
        let time = NSInteger(self)
       // let ms = Int((self.truncatingRemainder(dividingBy: 1)) * 1000)
        let seconds = time % 60
        let minutes = (time / 60) % 60
        let hours = (time / 3600)
       // return String(format: "%0.2d:%0.2d:%0.2d.%0.3d",hours,minutes,seconds,ms)
        if(hours == 0){
            if(minutes < 10){
                return String(format: "%0.1d:%0.2d",minutes,seconds)
            }
            return String(format: "%0.2d:%0.2d",minutes,seconds)
        }
        return String(format: "%0.2d:%0.2d:%0.2d",hours,minutes,seconds)
    }
}

//
//  CustomSlider.swift
//  VocalstarFramework
//
//  Created by Mathias Dietrich on 13.07.23.
//
//
//  CustomSlider.swift
//  vocalstar
//
//  Created by Mathias Dietrich on 04.06.23.
//
// https://swdevnotes.com/swift/2021/how-to-customise-the-slider-in-swiftui/

import SwiftUI

struct MouseClickActions: ViewModifier {
    var onMouseDown: () -> Void
    var onMouseUp: () -> Void
    func body(content: Content) -> some View {
        content
            .simultaneousGesture(
                DragGesture(minimumDistance: 0)
                    .onChanged({ _ in
                        onMouseDown()
                    })
                    .onEnded({ _ in
                        onMouseUp()
                    })
            )
    }
}

class AppState: ObservableObject {
    @Published var isTouched = false
}

struct SliderView2: View {
    @Binding var value: Double
    @State var lastCoordinateValue: CGFloat = 0.0
    @State var type: Int
    @StateObject var appState = AppState()
    
    var sliderRange: ClosedRange<Double> = 0...100
    
    func onChange(){
      Engine.shared.scroll(percent: value)
    }
    
    func onDone(){
        Engine.shared.scrollDone()
    }
    
    var body: some View {
        GeometryReader { gr in
            let thumbSize = gr.size.height * 1.2
            let radius = gr.size.height * 0.8
            let minValue : CGFloat = 0
            let maxValue = (gr.size.width) - thumbSize
            let scaleFactor = (maxValue - minValue) / (sliderRange.upperBound - sliderRange.lowerBound)
            let lower = sliderRange.lowerBound
            let sliderVal = (self.value - lower) * scaleFactor + minValue
            
            ZStack {
                RoundedRectangle(cornerRadius: radius)
                    .foregroundColor(Color.brown).frame(width:gr.size.width-8, height: 6)
                    .offset(x:-3,y:0).gesture(
                    
                DragGesture(minimumDistance: 0)
                    .onChanged { v in
                        if (abs(v.translation.width) < 0.1) {
                            self.lastCoordinateValue = sliderVal
                        }
                        if v.translation.width > 0 {
                            let nextCoordinateValue = min(maxValue, self.lastCoordinateValue + v.translation.width)
                            self.value = ((nextCoordinateValue - minValue) / scaleFactor)  + lower
                            onChange()
                        } else {
                            let nextCoordinateValue = max(minValue, self.lastCoordinateValue + v.translation.width)
                            self.value = ((nextCoordinateValue - minValue) / scaleFactor) + lower
                            onChange()
                        }
                    }
                    .onEnded({_ in
                        onDone()
                    })
                ).modifier(MouseClickActions(
                    onMouseDown: {
                        appState.isTouched  = true
                    },
                    onMouseUp: {
                        appState.isTouched  = false
                    }
                ))
                
                RoundedRectangle(cornerRadius: radius)
                    .foregroundColor(.white).frame(width:sliderVal + 3, height: appState.isTouched ? 12 : 6)
                    .position(x:sliderVal/2).offset(x:0,y:3).gesture(
            
                DragGesture(minimumDistance: 0)
                    .onChanged { v in
                        if (abs(v.translation.width) < 0.1) {
                            self.lastCoordinateValue = sliderVal
                        }
                        if v.translation.width > 0 {
                            let nextCoordinateValue = min(maxValue, self.lastCoordinateValue + v.translation.width)
                            self.value = ((nextCoordinateValue - minValue) / scaleFactor)  + lower
                            onChange()
                        } else {
                            let nextCoordinateValue = max(minValue, self.lastCoordinateValue + v.translation.width)
                            self.value = ((nextCoordinateValue - minValue) / scaleFactor) + lower
                            onChange()
                        }
                    }
                    .onEnded({_ in
                        onDone()
                    })
                )
                    .modifier(MouseClickActions(
                        onMouseDown: {
                            appState.isTouched  = true
                        },
                        onMouseUp: {
                            appState.isTouched  = false
                        }
                    ))
                HStack {
                    Spacer()
                }
            }
        }
    }
}


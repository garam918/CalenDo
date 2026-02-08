//
//  DooItBundle.swift
//  DooIt
//
//  Created by 임가람 on 11/16/25.
//

import WidgetKit
import SwiftUI

@main
struct DooItBundle: WidgetBundle {
    var body: some Widget {
        DooIt()
        DooItControl()
        DooItLiveActivity()
    }
}

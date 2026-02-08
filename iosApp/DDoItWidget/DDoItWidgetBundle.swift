//
//  DDoItWidgetBundle.swift
//  DDoItWidget
//
//  Created by 임가람 on 11/30/25.
//

import WidgetKit
import SwiftUI

@main
struct DDoItWidgetBundle: WidgetBundle {
    var body: some Widget {
        DDoItWidget()
        DDoItWidgetControl()
        DDoItWidgetLiveActivity()
    }
}

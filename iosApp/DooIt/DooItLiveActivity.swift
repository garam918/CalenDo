//
//  DooItLiveActivity.swift
//  DooIt
//
//  Created by 임가람 on 11/16/25.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct DooItAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct DooItLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: DooItAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension DooItAttributes {
    fileprivate static var preview: DooItAttributes {
        DooItAttributes(name: "World")
    }
}

extension DooItAttributes.ContentState {
    fileprivate static var smiley: DooItAttributes.ContentState {
        DooItAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: DooItAttributes.ContentState {
         DooItAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: DooItAttributes.preview) {
   DooItLiveActivity()
} contentStates: {
    DooItAttributes.ContentState.smiley
    DooItAttributes.ContentState.starEyes
}

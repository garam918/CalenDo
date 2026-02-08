//
//  DDoItWidget.swift
//  DDoItWidget
//
//  Created by 임가람 on 11/30/25.
//

import WidgetKit
import SwiftUI
import Shared

//struct SimpleEntry: TimelineEntry {
//    let date: Date
//    let widgetData: WidgetItem // KMP shared 모듈의 데이터 클래스
//}

struct Provider: TimelineProvider {
    
    private let repository: WidgetRepository = {
            // Xcode에서 설정한 App Group ID
            let appGroupId = "com.garam.shared"
            
            // App Group의 공유 디렉토리 경로 가져오기
            guard let containerURL = FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: appGroupId) else {
                fatalError("App Group 경로를 찾을 수 없습니다. Xcode 설정을 확인하세요.")
            }
            
            // 경로 문자열로 변환
            
            // Kotlin의 iosMain에 만든 함수 호출! -> DB 생성
            let database = getDatabaseBuilder()
            return WidgetRepository(database: database)
        }()
    
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(), emoji: "😀")
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let entry = SimpleEntry(date: Date(), emoji: "😀")
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        var entries: [SimpleEntry] = []

        // Generate a timeline consisting of five entries an hour apart, starting from the current date.
        let currentDate = Date()
        for hourOffset in 0 ..< 5 {
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            let entry = SimpleEntry(date: entryDate, emoji: "😀")
            entries.append(entry)
        }

        let timeline = Timeline(entries: entries, policy: .atEnd)
        completion(timeline)
    }

//    func relevances() async -> WidgetRelevances<Void> {
//        // Generate a list containing the contexts this widget is relevant in.
//    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let emoji: String
}

struct DDoItWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        VStack {
            Text("Time:")
            Text(entry.date, style: .time)

            Text("Emoji:")
            Text(entry.emoji)
        }
    }
}

struct DDoItWidget: Widget {
    let kind: String = "DDoItWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            if #available(iOS 17.0, *) {
                DDoItWidgetEntryView(entry: entry)
                    .containerBackground(.fill.tertiary, for: .widget)
            } else {
                DDoItWidgetEntryView(entry: entry)
                    .padding()
                    .background()
            }
        }
        .configurationDisplayName("My Widget")
        .description("This is an example widget.")
    }
}

#Preview(as: .systemSmall) {
    DDoItWidget()
} timeline: {
    SimpleEntry(date: .now, emoji: "😀")
    SimpleEntry(date: .now, emoji: "🤩")
}

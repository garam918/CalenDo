import FirebaseFirestoreInternal
import Foundation

@objc class FirestoreBridge: NSObject {

    @objc static func saveTodo(
        uid: String,
        id: String,
        categoryId: String,
        title: String,
        startDate: String,
        endDate: String,
        repeatRule: String,
        status: [String: String],
        priority: Bool,
        memo: String,
        icon: String,
        color: String,
        startTime: String,
        index: Int,
        savedTime: Int64
    ) {

        let db = Firestore.firestore()
        let todoData: [String: Any] = [
            "id": id,
            "categoryId": categoryId,
            "title": title,
            "startDate": startDate,
            "endDate": endDate,
            "repeatRule": repeatRule,
            "status": status,
            "priority": priority,
            "memo": memo,
            "icon": icon,
            "color": color,
            "startTime": startTime,
            "index": index,
            "savedTime": savedTime,
            "userId": uid
        ]

        db.collection("users").document(uid)
            .collection("todos").document(id)
            .setData(todoData) { error in
                if let error = error {
                    print("❌ Firestore 저장 실패: \(error.localizedDescription)")
                } else {
                    print("✅ Firestore 저장 성공: \(title)")
                }
            }
    }

    @objc static func deleteTodo(_ id: String, uid: String) {
        let db = Firestore.firestore()
        db.collection("users").document(uid)
            .collection("todos").document(id)
            .delete { error in
                if let error = error {
                    print("❌ Firestore 삭제 실패: \(error.localizedDescription)")
                } else {
                    print("✅ Firestore 삭제 성공: \(id)")
                }
            }
    }
}

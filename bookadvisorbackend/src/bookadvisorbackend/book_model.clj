(ns bookadvisorbackend.book-model
  "The book model."
  (:require [next.jdbc.sql :as sql]))

(defn get-chapters
  "Return all chapters."
  [db]
  (sql/query (db)
             ["
select c.id, c.name
 from chapters c
 order by c.id
"]))

(defn get-plots
  "Return all plots."
  [db]
  (sql/query (db)
             ["
select p.id, p.name
 from plots p
 order by p.id
"]))

(defn get-scenes
  "Return all scenes."
  [db]
  (sql/query (db)
             ["
select s.id, s.title, s.extract, s.value, s.chapter_id, s.plot_id
 from scenes s
 order by s.chapter_id, s.plot_id
"]))

(defn create-chapter
  "Create a chapter"
  [db chapter]
  (println "Create chapter" (:chapters/name chapter))
  (sql/insert! (db) :chapters
               (dissoc chapter :chapters/id)))

(defn update-chapter
  "Update a chapter"
  [db chapter]
  (let [chapterId (:chapters/id chapter)]
    (println "Update chapter" chapterId)
    (sql/update! (db) :chapters (dissoc chapter :chapters/id) ["id = ?" chapterId])))

(defn get-chapter
  "Get a chapter"
  [db chapterId]
  (println "Get chapter" chapterId)
  (sql/get-by-id (db) :chapters chapterId))
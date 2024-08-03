(ns bookadvisorbackend.book-model
  "The book model."
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

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

(defn delete-chapter
  "Delete a chapter"
  [db id]
  (let [chapterId (Integer/parseInt id)]
    (println "Delete chapter" chapterId)
    (sql/delete! (db) :chapters ["id = ?" chapterId])))

(defn create-plot
  "Create a plot"
  [db plot]
  (println "Create plot" (:plots/name plot))
  (sql/insert! (db) :plots
               (dissoc plot :plots/id)))

(defn update-plot
  "Update a plot"
  [db plot]
  (let [plotId (:plots/id plot)]
    (println "Update plot" plotId)
    (sql/update! (db) :plots (dissoc plot :plots/id) ["id = ?" plotId])))

(defn get-plot
  "Get a plot"
  [db plotId]
  (println "Get plot" plotId)
  (sql/get-by-id (db) :plots plotId))

(defn delete-plot
  "Delete a plot"
  [db id]
  (let [plotId (Integer/parseInt id)]
    (println "Delete plot" plotId)
    (sql/delete! (db) :plots ["id = ?" plotId])))

(defn create-scene
  "Create a scene"
  [db scene]
  (println "Create scene" (:scenes/title scene))
  (sql/insert! (db) :scenes
               (dissoc scene :scenes/id)))

(defn update-scene
  "Update a scene"
  [db scene]
  (let [sceneId (:scenes/id scene)]
    (println "Update scene" sceneId)
    (sql/update! (db) :scenes (dissoc (dissoc scene :scenes/id) :scenes/value) ["id = ?" sceneId])))

(defn update-scene-value
  "Update a scene"
  [db scene]
  (let [sceneId (:scenes/id scene)]
    (println "Update scene" scene)
    (jdbc/execute-one! (db) ["UPDATE scenes SET value = ? WHERE id = ?" (:scenes/value scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET extract = ? WHERE id = ?" (:scenes/extract scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET chapter_id = ? WHERE id = ?" (:scenes/chapter_id scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET plot_id = ? WHERE id = ?" (:scenes/plot_id scene) sceneId])))

(defn get-scene
  "Get a scene"
  [db sceneId]
  (println "Get scene" sceneId)
    (sql/get-by-id (db) :scenes sceneId))

(defn get-scene-value
  "Get a scene"
  [db id]
  (let [sceneId (Integer/parseInt id)]
    (println "Get scene" sceneId)
    (sql/get-by-id (db) :scenes sceneId)))
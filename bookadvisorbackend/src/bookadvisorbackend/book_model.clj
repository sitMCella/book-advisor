(ns bookadvisorbackend.book-model
  "The book model."
  (:import   [java.sql Array])
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(extend-protocol rs/ReadableColumn
  Array
  (read-column-by-label [^Array v _]    (vec (.getArray v)))
  (read-column-by-index [^Array v _ _]  (vec (.getArray v))))

(defn get-projects
  "Return all projects"
  [db]
  (sql/query (db)
             ["
select id, name, description, tags
 from projects
 order by id
"]))

(defn create-project
  "Create a project"
  [db project]
  (println "Create project" (:projects/name project))
  (sql/insert! (db) :projects
               (dissoc project :projects/id)))

(defn update-project
  "Update a project"
  [db project]
  (let [projectId (:projects/id project)
        projectTags (into-array String (:projects/tags project))]
    (println "Update project" project)
    (sql/update! (db) :projects (assoc (dissoc (dissoc project :projects/id) :projects/tags) :projects/tags projectTags) ["id = ?" projectId])))

(defn get-project
  "Get a project"
  [db projectId]
  (println "Get project" projectId)
  (sql/get-by-id (db) :projects projectId))

(defn delete-project
  "Delete a project"
  [db id]
  (let [projectId (Integer/parseInt id)]
    (println "Delete project" projectId)
    (sql/delete! (db) :projects ["id = ?" projectId])))

(defn get-chapters
  "Return all chapters."
  [db id]
  (let [projectId (Integer/parseInt id)]
    (sql/query (db)
               ["
select id, name, project_id
 from chapters
 where project_id = ?
 order by id
" projectId])))

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

(defn get-plots
  "Return all plots."
  [db id]
  (let [projectId (Integer/parseInt id)]
    (sql/query (db)
               ["
select id, name, project_id
 from plots
 where project_id = ?
 order by id
" projectId])))

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

(defn get-scenes
  "Return all scenes."
  [db id]
  (let [projectId (Integer/parseInt id)]
    (sql/query (db)
               ["
select id, title, extract, value, chapter_id, plot_id, project_id, tags
 from scenes
 where project_id = ?
 order by chapter_id, plot_id
" projectId])))

(defn create-scene
  "Create a scene"
  [db scene]
  (let [sceneTags (into-array String (:scenes/tags scene))]
    (println "Create scene" (:scenes/title scene))
    (sql/insert! (db) :scenes
                 (assoc (dissoc (dissoc scene :scenes/tags) :scenes/id) :scenes/tags sceneTags))))

(defn update-scene
  "Update a scene"
  [db scene]
  (let [sceneId (:scenes/id scene)]
    (println "Update scene" sceneId)
    (sql/update! (db) :scenes (dissoc (dissoc (dissoc scene :scenes/id) :scenes/value) :scenes/tags) ["id = ?" sceneId])))

(defn update-scene-value
  "Update scene values"
  [db scene]
  (let [sceneId (:scenes/id scene)
        sceneTags (into-array String (:scenes/tags scene))]
    (println "Update scene" scene)
    (jdbc/execute-one! (db) ["UPDATE scenes SET value = ? WHERE id = ?" (:scenes/value scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET extract = ? WHERE id = ?" (:scenes/extract scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET chapter_id = ? WHERE id = ?" (:scenes/chapter_id scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET plot_id = ? WHERE id = ?" (:scenes/plot_id scene) sceneId])
    (jdbc/execute-one! (db) ["UPDATE scenes SET tags = ? WHERE id = ?" sceneTags sceneId])))

(defn get-scene
  "Get a scene"
  [db sceneId]
  (println "Get scene" sceneId)
  (sql/get-by-id (db) :scenes sceneId))

(defn delete-scene
  "Delete a scene"
  [db id]
  (let [sceneId (Integer/parseInt id)]
    (println "Delete scene" sceneId)
    (sql/delete! (db) :scenes ["id = ?" sceneId])))
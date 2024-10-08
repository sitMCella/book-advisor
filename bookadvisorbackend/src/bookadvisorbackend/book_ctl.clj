(ns bookadvisorbackend.book-ctl
  "The controller for the book management."
  (:require [bookadvisorbackend.http-manager :as manager]
            [bookadvisorbackend.book-model :as model]
            [clojure.data.json :as json]
            [clojure.string :as string]))

(defn get-projects
  "Retrieve the projects."
  [req]
  (let [projects (model/get-projects (-> req :application/component :database))]
    (println projects)
    (manager/json-response projects)))

(defn create-project
  "Create new project"
  [req]
  (let [project (json/read-str (slurp (:body req)) :key-fn keyword)
        createdProject (model/create-project (-> req :application/component :database) project)]
    (manager/json-response createdProject)))

(defn update-project
  "Update project"
  [req]
  (let [project (json/read-str (slurp (:body req)) :key-fn keyword)]
    (model/update-project (-> req :application/component :database) project)
    (manager/json-response (model/get-project (-> req :application/component :database) (:projects/id project)))))

(defn get-project
  "Get project"
  [req]
  (let [id (get (string/split (:uri req) #"/") 3)]
    (manager/json-response (model/get-project (-> req :application/component :database) (Integer/parseInt id)))))

(defn delete-project
  "Delete project"
  [req]
  (let [id (get (string/split (:uri req) #"/") 3)]
    (model/delete-project (-> req :application/component :database) id)
    (manager/json-response {})))

(defn get-chapters
  "Retrieve the chapters."
  [req]
  (let [projectId (get (string/split (:uri req) #"/") 3)
        chapters (model/get-chapters (-> req :application/component :database) projectId)]
    (println chapters)
    (manager/json-response chapters)))

(defn create-chapter
  "Create new chapter"
  [req]
  (let [chapter (json/read-str (slurp (:body req)) :key-fn keyword)
        createdChapter (model/create-chapter (-> req :application/component :database) chapter)]
    (manager/json-response createdChapter)))

(defn update-chapter
  "Update chapter"
  [req]
  (let [chapter (json/read-str (slurp (:body req)) :key-fn keyword)]
    (model/update-chapter (-> req :application/component :database) chapter)
    (manager/json-response (model/get-chapter (-> req :application/component :database) (:chapters/id chapter)))))

(defn delete-chapter
  "Delete chapter"
  [req]
  (let [id (get (string/split (:uri req) #"/") 5)]
    (model/delete-chapter (-> req :application/component :database) id)
    (manager/json-response {})))

(defn get-plots
  "Retrieve the plots."
  [req]
  (let [projectId (get (string/split (:uri req) #"/") 3)
        plots (model/get-plots (-> req :application/component :database) projectId)]
    (println plots)
    (manager/json-response plots)))

(defn create-plot
  "Create new plot"
  [req]
  (let [plot (json/read-str (slurp (:body req)) :key-fn keyword)
        createdPlot (model/create-plot (-> req :application/component :database) plot)]
    (manager/json-response createdPlot)))

(defn update-plot
  "Update plot"
  [req]
  (let [plot (json/read-str (slurp (:body req)) :key-fn keyword)]
    (model/update-plot (-> req :application/component :database) plot)
    (manager/json-response (model/get-plot (-> req :application/component :database) (:plots/id plot)))))

(defn delete-plot
  "Delete plot"
  [req]
  (let [id (get (string/split (:uri req) #"/") 5)]
    (model/delete-plot (-> req :application/component :database) id)
    (manager/json-response {})))

(defn get-scenes
  "Retrieve the scenes."
  [req]
  (let [projectId (get (string/split (:uri req) #"/") 3)
        scenes (model/get-scenes (-> req :application/component :database) projectId)]
    (println scenes)
    (manager/json-response scenes)))

(defn create-scene
  "Create new scene"
  [req]
  (let [scene (json/read-str (slurp (:body req)) :key-fn keyword)
        createdScene (model/create-scene (-> req :application/component :database) scene)]
    (manager/json-response createdScene)))

(defn update-scene
  "Update scene"
  [req]
  (let [scene (json/read-str (slurp (:body req)) :key-fn keyword)]
    (model/update-scene (-> req :application/component :database) scene)
    (manager/json-response (model/get-scene (-> req :application/component :database) (:scenes/id scene)))))

(defn get-scene
  "Retrieve the scene."
  [req]
  (let [id (get (string/split (:uri req) #"/") 5)
        scene (model/get-scene (-> req :application/component :database) (Integer/parseInt id))]
    (println scene)
    (manager/json-response scene)))

(defn update-scene-value
  "Update scene"
  [req]
  (let [scene (json/read-str (slurp (:body req)) :key-fn keyword)]
    (println scene)
    (model/update-scene-value (-> req :application/component :database) scene)
    (manager/json-response (model/get-scene (-> req :application/component :database) (:scenes/id scene)))))

(defn delete-scene
  "Delete scene"
  [req]
  (let [id (get (string/split (:uri req) #"/") 5)]
    (model/delete-scene (-> req :application/component :database) id)
    (manager/json-response {})))
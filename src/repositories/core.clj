(ns repositories.core
  (:gen-class)
  (:use [cheshire.core :only [generate-string parse-string]]
        [clj-http.client :only [get] :rename {get hget}]
        [overtone.at-at :only [after every mk-pool]]
        [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.resource :only [wrap-resource]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.util.response :only [content-type response]]))

(defn- make-repos-handler [repos]
  (fn [req]
    (when (= "/repositories.json" (:uri req))
      (-> (response (generate-string (flatten @repos)))
          (content-type "application/json")))))

(defn- wrap-index [handler]
  (fn [req]
    (handler (update-in req [:uri] #(if (= % "/") "/index.html" %)))))

(defn- app [repos]
  (-> (make-repos-handler repos)
      (wrap-resource "public")
      (wrap-file-info)
      (wrap-index)))

(def repo-attributes
  ["description" "html_url" "name" "private" "pushed_at"])

(defn- parse-repos [json]
  (let [repos (parse-string json)]
    (map #(select-keys % repo-attributes) repos)))

(defn- get-repos [url]
  (let [href-fn #(get-in % [:links :next :href])
        repo-fn #(conj %1 (parse-repos (:body %2)))]
    (loop [res (hget url) acc []]
      (if (nil? (href-fn res))
        (repo-fn acc res)
        (recur (hget (href-fn res)) (repo-fn acc res))))))

(defn- update-repos [repos url wait sleep]
  (let [update-fn #(reset! repos (get-repos url))
        pool (mk-pool)]
    (after wait update-fn pool)
    (every sleep update-fn pool)))

(defn -main []
  (let [port (Integer. (System/getenv "PORT"))
        sleep (Integer. (System/getenv "SLEEP"))
        url (System/getenv "URL")
        repos (atom [])]
    (update-repos repos url 1000 sleep)
    (run-jetty (app repos) {:port port})))

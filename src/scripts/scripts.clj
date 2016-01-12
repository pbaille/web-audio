(ns scripts.scripts
  (require [clojure.data.codec.base64 :as b64]
           [clojure.java.io :as io]
           [alex-and-georges.debug-repl :as drepl]
           [bartok.primitives :refer [pitches]])
  (import java.io.File))

(defmacro dr
  ([] `(drepl/debug-repl))
  ([& args] `(do (drepl/debug-repl) ~@args )))
  
;;little script to rename sound files

;;little script to rename sound files
(defn name-by-midi-num [path prefix format]
  (doseq [x (filter seq pitches)]
    (doto (File. (str path "/" prefix (name (:name (:pitch-class x))) (+ (:octave x) 5) "." format)) 
      (.renameTo (File. (str path "/" (:val x) "." format))))))

; (name-by-midi-num "sounds/glass_harmo" "GLAR_MA_ES_p1_" "ogg")
; (name-by-midi-num "sounds/vibra" "Vib_ES_Me_sp-0_mf1_" "ogg")
; (name-by-midi-num "sounds/celesta" "CE_ES_mf_" "mp3")

(defn write-file [path content]
  (with-open [w (clojure.java.io/writer path :append true)]
    (.write w content)))

(defn file-name-wo-ext [f]
  (last (butlast (clojure.string/split (.getName f) #"\.|\/"))))

(defn make-sf 
  "from a dir of samples, make a sounfont js file for use in MIDI.js
   path: path of the directory containing samples
   name: name of the instrument
   type: format of samples (ogg, mp3 etc)
   out-file: path for the generated js file (out-dir/)"
  [path name type out-path]
  (let [out-file (str out-path name ".js")]
    (write-file out-file 
      (str "if (typeof(MIDI) === 'undefined') var MIDI = {};\nif (typeof(MIDI.Soundfont) === 'undefined') MIDI.Soundfont = {};\nMIDI.Soundfont." name " = {\n"))
    (doseq [f (next (file-seq (File. path)))
          :let [wo-ext (file-name-wo-ext f)
                tmp-path (str path wo-ext ".tmp")]]
      (with-open [in (io/input-stream f)
                  out (io/output-stream tmp-path)]
        (b64/encoding-transfer in out)
        (let [s (slurp tmp-path)]
          (write-file out-file (str "  '" wo-ext "': 'data:audio/" type ";base64," s "',\n"))
          (.delete (File. tmp-path)))))
    (write-file out-file "}")))

; (make-sf "sounds/glass_harmo" "harmo" "ogg" "sounds/")


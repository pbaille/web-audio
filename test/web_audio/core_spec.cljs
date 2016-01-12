(ns web-audio.core-spec
  (:require-macros 
    [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]])
  (:require [cemerick.cljs.test :as t]
            [web-audio.core :as wa]))

(deftest foo-test
  (is (wa/foo) "foo"))
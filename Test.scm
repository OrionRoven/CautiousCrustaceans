(let x 0)(loop (
  (println x)(let x (+ x 1))(if ((break)) ((pass)) (> x 25))
 ))
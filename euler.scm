(let max 1000)(let counter 1)(let e 0.0)(let denom 1.0)
(loop (
  (let e (+ e (/ 1.0 denom)))
  (let denom (* counter denom))
  (let counter (+ counter 1))
  (if ((break)) ((pass)) (> counter max))
))
(println e)
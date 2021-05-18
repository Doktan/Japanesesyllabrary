const { Router} = require("express")
const router = Router()
const connection = require("../index.js");


function shuffle(array) {
  array.sort(() => Math.random() - 0.5);
}

function isEmpty(obj) {
  return Object.keys(obj).length === 0;
}

let syll = ['a', 'i', 'u', 'e', 'o', 'ka', 'ki', 'ku', 'ke', 'ko', 'sa', 'shi', 'su',
  'se', 'so', 'ta', 'chi', 'tsu', 'te', 'to', 'na', 'ni', 'nu', 'ne', 'no', 'ha', 'hi',
  'fu', 'he', 'ho', 'ma', 'mi', 'mu', 'me', 'mo', 'ya', 'yu', 'yo', 'ra', 'ri', 'ru', 're', 'ro',
  'wa', 'wo', 'n']

router.post('/getGame', (req, res) => {
  const mode = req.body.mode
  console.log(mode)
  switch (mode) {
    case 'Classic': {
      let prefix = 'h_'
      shuffle(syll)
      console.log(syll)
      return res.status(200).json({ message: 'ok', prefix: prefix, syll: syll })
    }
    case 'Classic (Katakana)': {
      let prefix = 'k_'
      shuffle(syll)
      console.log(syll)
      return res.status(200).json({ message: 'ok', prefix: prefix, syll: syll })
    }
    default:
      break
  }
  return res.status(200).json({ message: 'BRUH' })
});

/*router.post('/getKana',(req,res)=>{
  const body = req.body
  console.log('prefix ', body.prefix ,'kana ', body.kana)
  let url = 'F:/everything/AnrdoidApps/Japanesesyllabrary/server/res/workHiragana/' + body.prefix + body.kana + '.png'
  console.log(url)
  res.status(200).sendFile(url)
})
*/

router.post('/record', (req, res) => {
  const body = req.body
  console.log(body)
  let user_id
  let mode_id
  let record = body.record
  // score, name, mode
  const query1 = 'SELECT user_id from users where name = ?'
  connection.query(query1, [body.name], (errs1, results1) => {
    if (errs1)
      return res.status(500).json({ message: "Internal server error" })
    else {
      console.log(results1)
      if (isEmpty(results1))
        return res.status(200).json({ message: "There is no such a user" })
      else {
        user_id = results1[0].user_id
        const query2 = 'SELECT id_mode from modes where mode_title = ?'
        connection.query(query2, [body.mode], (err2, results2) => {
          if (err2) {
            console.log("modes error ", err2)
            return res.status(500).json({ message: "internal server error" })
          }
          else
            if (isEmpty(results2))
              return res.status(200).json({ message: "There is no such a gamemode" })
            else {
              console.log(results2)
              mode_id = results2[0].id_mode
              console.log("add mode_id ", mode_id, " add user_id: ", user_id, "add record: ", record)
              const query3 = 'INSERT INTO `records`(`id_mode`, `id_user`, `record`) VALUES (?,?,?)'
              connection.query(query3, [mode_id, user_id, record], (err3, results3) => {
                if (err3) {
                  console.log("err add record ", err3)
                  res.status(500).json({ message: "Internal server error" })
                }
                if (results3) {
                  console.log("res!!!")
                  return res.status(200).json({ message: "ok" })
                }
              })
            }
        })
      }
    }
  })




  //return res.status(200).json({message: "bruh"})
})

module.exports = router;

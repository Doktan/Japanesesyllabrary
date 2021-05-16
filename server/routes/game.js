const { Router, urlencoded } = require("express")
const router = Router()
const connection = require("../index.js")

function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
  }

let syll = ['a','i','u','e','o','ka','ki','ku','ke','ko','sa','shi','su',
  'se','so','ta','chi','tsu','te','to','na','ni','nu','ne','no','ha','hi',
  'fu','he','ho','ma','mi','mu','me','mo','ya','yu','yo','ra','ri','ru','re','ro',
  'wa','wo','n']

router.post('/getGame',(req,res)=>{
    const mode = req.body.mode
    console.log(mode)
    switch(mode){
        case 'Classic':{
            let prefix = 'h_'
            shuffle(syll)
            console.log(syll)
            return res.status(200).json({message: 'ok', prefix: prefix, syll: syll})
          }
        case 'Classic (Katakana)':{
            let prefix = 'k_'
            shuffle(syll)
            console.log(syll)
            return res.status(200).json({message: 'ok', prefix: prefix, syll: syll})
          }
        default:
            break
    }
    return res.status(200).json({message: 'BRUH'})
});

/*router.post('/getKana',(req,res)=>{
  const body = req.body
  console.log('prefix ', body.prefix ,'kana ', body.kana)
  let url = 'F:/everything/AnrdoidApps/Japanesesyllabrary/server/res/workHiragana/' + body.prefix + body.kana + '.png'
  console.log(url)
  res.status(200).sendFile(url)
})
*/
module.exports = router;
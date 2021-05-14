const { Router } = require("express")
const router = Router()
const connection = require("../index.js")

function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
  }

let syll = ['a','i','u','e','o','ka','ki','ku','ke','ko','sa','shi','su',
  'se','so','ta','chi','tsu','te','to','na','ni','nu','ne','no','ha','hi',
  'hu','he','ho','ma','mi','mu','me','mo','ya','yu','yo','ra','ri','ru','re','ro',
  'wa','wo','n']

router.post('/getGame',(req,res)=>{
    const mode = req.body.mode
    console.log(mode)
    switch(mode){
        case 'Classic':
            let prefix = 'h_'
            shuffle(syll)
            return res.status(200).json({message: 'ok', prefix: prefix, syll: syll})
        default:
            break
    }
    return res.status(200).json({message: 'BRUH'})
});

module.exports = router;
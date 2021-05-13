const { Router } = require("express")
const router = Router()
const connection = require("../index.js")

router.get('/get', (req,res)=>{
    const query1 = 'SELECT `mode_title` from modes'
    const query2 = 'SELECT `description` from modes'
    connection.query(query1,(err,result)=>{
        if(err){
            console.log("ошибка get modes ", err)
            return res.status(500).json({message: "internal server error"})
        }
        if(result){
            console.log("debug ", result[0].mode_title)
            let arrayModes = []
            for(let i = 0; i < result.length; i++)
                arrayModes[i] = result[i].mode_title
            console.log("debug ", arrayModes)
            connection.query(query2, (err2,resut2)=>{
                if(err2){
                    console.log("ошибка get modes во время получения описаний! ", err2)
                    return res.status(500).json({message: "internal server error"})
                }
                if(resut2){
                    let arrayDiscriptions = []
                    for(let i = 0; i < resut2.length; i++)
                        arrayDiscriptions[i] = resut2[i].description
                    console.log(result, " and ", resut2)
                    return res.status(200).json({message: "ok", modes: arrayModes, discriptions: arrayDiscriptions})
                }
            })
        }
    })
})

module.exports = router;

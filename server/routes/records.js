const { Router } = require("express")
const router = Router()
const connection = require("../index.js");

router.get('/get', (req,res)=>{
    const query1 = 'SELECT `mode_title` from modes'
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
            return res.status(200).json({message: "ok", data: arrayModes})
        }
    })
})

router.post('/getRecords',(req,res)=>{
    const mode_title = req.body.mode
    console.log(mode_title)
    const query1 = 'SELECT id_mode from modes where mode_title = ?'
    const query2 = `SELECT users.name, records.record from records
    INNER JOIN users
    ON
    records.id_user = users.user_id
    WHERE records.id_mode = ?
    ORDER BY records.record DESC
    LIMIT 0,10`
    connection.query(query1,[mode_title],(err1,result1)=>{
        if(err1){
            console.log("server error ", err1)
            return res.status(500).json({message: "internal server error"})
        }
        if(result1){
            let mode_id = result1[0].id_mode
            console.log("mode_id ", mode_id)
            connection.query(query2,[mode_id],(err2,result2)=>{
                if(err2){
                    console.log("error in records ", err2)
                    return res.status(500).json({message: "internal server error"})
                }
                if(result2){
                    let names = []
                    let records = []
                    for(let i = 0 ; i < result2.length; i++)
                        names[i] = result2[i].name
                    for(let i = 0 ; i < result2.length; i++)
                        records[i] = result2[i].record
                    console.log("ITOGO: ", names, " and ", records)
                    return res.status(200).json({message: 'ok', names: names, records: records})
                }
            })
        }

    })
})

module.exports = router;

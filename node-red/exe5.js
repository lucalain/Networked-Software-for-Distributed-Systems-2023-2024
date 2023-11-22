const prompt = require('prompt-sync')({sigint: true});

let n = 0;
let sum = 0;
let min = Number.MAX_VALUE
let max = Number.MIN_VALUE

while(true){

    let temp = Number(prompt("insert temperature [0 for exit]:"))
    if(temp === 0){
        break
    }
    if(temp<min){
        min = temp
    }
    if(temp>max){
        max = temp
    }
    sum = sum + temp;
    n++;
}

console.log('min temp: ' + min)
console.log('max temp: ' + max)
console.log('avg temp: ' + sum/n)
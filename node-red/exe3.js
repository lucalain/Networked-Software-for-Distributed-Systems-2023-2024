
/*
Refactor the code of Exercise 2 and create 2 functions
— One that displays the prime numbers from 2 to n
— One that checks whether a given h is prime or not
*/

// 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 
// 59, 61, 67, 71, 73, 79, 83, 89, 97

function prime(a){
    let notPrime = false;
    for(let i =2;i<=Math.sqrt(a);i++){
        if(a%i == 0){
            notPrime = true;
            break;
        }
    }
    return !notPrime;
}
function primeDisp(n){
    for(let j = 1;j<n;j++){
        if(prime(j)){
            console.log(j);
        }
    }
}


const prompt = require('prompt-sync')({sigint: true});

let n = prompt("insert number:");
primeDisp(n);

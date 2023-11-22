
/*
Write a JavaScript program to compute the
prime numbers in an interval from 2 to n
— The value of n is input from the user
— The remainder operator % works as expected
*/

// 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 
// 59, 61, 67, 71, 73, 79, 83, 89, 97

const prompt = require('prompt-sync')({sigint: true});

let n = prompt("insert number:")
let notPrime = false;
for(let j = 1;j<n;j++){
    notPrime = false;
    for(let i =2;i<=Math.sqrt(j);i++){
        if(j%i == 0){
            notPrime = true;
            break;
        }
    }
    if(!notPrime){
        console.log(j);
    }
}

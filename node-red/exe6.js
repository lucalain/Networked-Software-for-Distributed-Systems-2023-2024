/*
Using the functions defined in Exercise 3,
create an array of objects where each object has two keys
– A number k from 2 to n, with n input from the user
– An array of all prime numbers up to k
*/

const prompt = require('prompt-sync')({sigint: true});

function Obj(n){
    this.number = n;
    this.prime = function(a){
        let notPrime = false;
        for(let i =2;i<=Math.sqrt(a);i++){
            if(a%i == 0){
                notPrime = true;
                break;
            }
        }
        return !notPrime;
    }
    this.primeDisp= function(n){
        let array = []
        for(let j = 1;j<n;j++){
            if(this.prime(j)){
                array.push(j)
            }
        }
        return array;
    }
    this.array = this.primeDisp(n);
}

let n = prompt("Insert n:");

let u = new Obj(n);
console.log("number:" + u.number + " array:" + u.array);
//This program translates a Psh program to a Schush program, which mainly
//involves making everything lowercase.

#include <iostream>

using namespace std;

int main(){

  char c;
  while(cin.get(c)){
    if(c >= 'A' && c <= 'Z'){
      c = c - 'A' + 'a';
    }
    cout << c;
  }
  

}

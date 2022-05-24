# ZR-Compiler
### Small Compiler for ZR-Language written in Java.

ZR Grammar implemented as follows:

Start -> Commands  
Commands -> Command Commands \| EPSILON  
Command -> WH Number \[ Commands ]    
       &emsp;&emsp;&emsp;&emsp;&ensp; | RE Number  
       &emsp;&emsp;&emsp;&emsp;&ensp; | VW Number  
       &emsp;&emsp;&emsp;&emsp;&ensp; | STIFT Number  
       &emsp;&emsp;&emsp;&emsp;&ensp; | FARBE ColorValue

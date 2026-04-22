# Contributions

I worked on the project alone so I did all the tasks.

# Use of AI tools

I have used a tool for this project, namely ChatGPT.
## Problem 1.2: Bonus

Here the the cases I was able to identify that needed less time to compute:
1. if the divisor is 1 then a/1 = a, which was done by using a for loop to connect the dividend bit values directly to the quotient vector and setting remainder = 0.
2. if the divisor is 0 I return the greatest possible number given the bitwidth, which is by iterating through the vector of quotient and setting each element equal to 1 and setting the remainder equal to the dividend io.
3. if the dividend is 0 then i return 0 given the bitwidth by setting all elements of the quotient vector equal to 0 and setting the remainder equal to 0.
4. dividend is equal to the divisor and both are non zero: return the quotient as 1 by making only 1 element equal to it and the rest equal to 0, and remainder is set to 0.
5. if the divisor is greater than the dividend then we directly return the quotient as 0 and the remainder as the dividend through connections of the io port.

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main() {

    srand(time(0));
    int d, j;

    printf("Please enter the distance of the position from the starting point. \n");
    scanf("%d", &d);

    printf("Please enter the maximum jumping distance of the frog. \n");
    scanf("%d", &j);

    int spots[d+1];
    for(int i = 0; i < d+1; i++) {
        spots[i] = 0;
    }

    for(int i = 0; i < 10000; i++) {
        
        int loc = 0;

        while(loc <= d) {
            spots[loc]++;
            loc = loc + (rand() % j) + 1;
        }

    }

    for(int i = 0; i < d+1; i++) {
        printf("%d %.4f\n", i, spots[i] / 10000.0);
    }

}
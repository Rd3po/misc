#include <stdio.h>

double calculateProb(int n, int p, double probs[100]) {
    if(n == 0) return 1;
    if(n < 0) return 0;
    if(probs[n] != 1.1) return probs[n];

    double sum = 0;
    for(int i = 1; i <= p; i++) {
        sum += (1.0 / p) * calculateProb(n - i , p, probs);
    }

    if(probs[n] == 1.1) {
        probs[n] = sum;
    }

    return sum;
}

int main() {

    double probs[100+1];

    int inputs = 0;
    scanf("%d", &inputs);

    probs[0] = 1;
    for(int i = 0; i < inputs; i++) {

        int x;
        int p;
        scanf("%d %d", &x, &p);

        for(int i = 1; i <= 100; i++) {
            probs[i] = 1.1;
        }

        printf("%.9f\n", calculateProb(x, p, probs));
    }

}
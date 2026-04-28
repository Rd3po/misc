/*============================================================================
| Assignment: pa02 - checksum
|
| Author: Ryan Demaria
| Language: c
| To Compile: gcc -o pa02 pa02.c 
“I [RYAN DEMARIA] ([RY054962]) affirm that
this program is entirely my own work and that I have neither developed my code with any
another person, nor copied any code from any other person, nor permitted my code to be copied
or otherwise used by any other person, nor have I copied, modified, or otherwise used programs
created by others. I acknowledge that any violation of the above terms will be treated as
academic dishonesty.” 

| To Execute: c -> ./pa02 X.txt #
| where kX.txt is the keytext file
| 
| Note:
| All input files are simple ASCII input
| All execute commands above have been tested on Eustis
|
| Class: CIS3360 - Security in Computing - Summer 2024
| Instructor: McAlpin
| Due Date: per assignment
+===========================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Function to calculate the checksum
unsigned long calculateChecksum(char *data, int dataSize, int checksumSize) {
    unsigned long checksum = 0;
    int i;

    for (i = 0; i < dataSize; ++i) {
        checksum += data[i];
    }

    // Shift the checksum to fit the specified size
    checksum = checksum & ((1 << checksumSize) - 1);

    return checksum;
}

int main(int argc, char *argv[]) {
    // Check if correct number of command line arguments are provided
    if (argc != 3) {
        fprintf(stderr, "Usage: %s <input_file> <checksum_size>\n", argv[0]);
        return 1;
    }

    // Parse command line arguments
    char *inputFileName = argv[1];
    int checksumSize = atoi(argv[2]);

    // Check if checksum size is valid
    if (checksumSize != 8 && checksumSize != 16 && checksumSize != 32) {
        fprintf(stderr, "Valid checksum sizes are 8, 16, or 32\n");
        return 1;
    }

    // Open the input file
    FILE *inputFile = fopen(inputFileName, "r");
    if (inputFile == NULL) {
        fprintf(stderr, "Error opening input file\n");
        return 1;
    }

    // Read the input data
    char buffer[81]; // Buffer to read 80 characters + null terminator
    char data[8192]; // Assuming maximum input file size of 8192 characters
    int dataSize = 0;
    while (fgets(buffer, sizeof(buffer), inputFile)) {
        strcpy(data + dataSize, buffer); // Concatenate buffer to data
        dataSize += strlen(buffer);
    }
    fclose(inputFile);

    // Echo the input text
    printf("%s", data);

    // Calculate the checksum
    unsigned long checksum = calculateChecksum(data, dataSize, checksumSize);

    // Print the checksum
    int characterCnt = dataSize;
    printf("%d bit checksum is %lx for all %d chars\n", checksumSize, checksum, characterCnt);

    return 0;
}
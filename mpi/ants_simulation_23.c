#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <mpi.h>
#include <math.h>

/*
 * Group number: 23
 *
 * Group members
 *  - Lain Luca 10726790
 *  - Mosconi Alessandro 10681624
 *  - Piaggio Martino 10686518
 */

const float min = 0;
const float max = 1000;
const float len = max - min;
const int num_ants = 8 * 1000 * 1000;
const int num_food_sources = 10;
const int num_iterations = 500;

float random_position() {
  return (float) rand() / (float)(RAND_MAX/(max-min)) + min;
}

/*
 * Process 0 invokes this function to initialize food sources.
 */
void init_food_sources(float* food_sources) {
  for (int i=0; i<num_food_sources; i++) {
    food_sources[i] = random_position();
  }
}

/*
 * Process 0 invokes this function to initialize the position of ants.
 */
void init_ants(float* ants) {
  for (int i=0; i<num_ants; i++) {
    ants[i] = random_position();
  }
}


float sum( float *ants, int size) {
  float sum = 0;
  for (int i=0; i<size; i++) {
    sum += ants[i];
  }
  return sum;
}


// If d1 is the distance between the ant 
// and the nearest source of food, the strength of F1 is 0.01*d1
float computeF1(float pos_ant, float *food_source){
    float d1 = max + 1; 
    float F1 = 0;
    
    for (int i=0; i<num_food_sources; i++) {
      float d =  food_source[i] - pos_ant;
      if (fabs(d) < fabs(d1)) {
        d1 = d;
      }
    }
    return 0.01 * d1;
}


// F2 attracts the ant towards the center of the colony
// If d2 is the distance between the ant and the center of the
// colony, the strength of F2 is 0.012*d2
  float computeF2(float pos_ant, float center){
    float d2 = center - pos_ant ;
    return 0.012 * d2; 
  }


int main() {
  MPI_Init(NULL, NULL);
    
  int rank;
  int num_procs;
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);
  MPI_Comm_size(MPI_COMM_WORLD, &num_procs);

  srand(rank);

  // Allocate space in each process for food sources and ants
  float *food_source = (float*) malloc(num_food_sources * sizeof(float));  
  
  float *ants = NULL;
  int num_local_ants = num_ants / num_procs;
  float *local_ants = (float*) malloc(num_local_ants * sizeof(float));
  
  // Process 0 initializes food sources and ants
  if(rank == 0){
    ants = (float*) malloc(num_ants * sizeof(float));
    init_food_sources(food_source);
    init_ants(ants);
  }

  // Process 0 distributed food sources and ants
  MPI_Scatter( ants , num_local_ants , MPI_FLOAT , local_ants , num_local_ants , MPI_FLOAT , 0 , MPI_COMM_WORLD);
  MPI_Bcast( food_source , num_food_sources , MPI_FLOAT , 0 , MPI_COMM_WORLD);

  // Iterative simulation
  float center = 0;
  float local_sum = sum(local_ants, num_local_ants);
  float total_sum = 0;
  
  MPI_Allreduce(&local_sum, &total_sum ,  1 , MPI_FLOAT , MPI_SUM , MPI_COMM_WORLD);

  center = total_sum / (float)(num_ants);

  for (int iter=0; iter<num_iterations; iter++) {
  
    for(int i =0; i< num_local_ants; i++){
      float f1 = computeF1(local_ants[i], food_source);
      float f2 = computeF2(local_ants[i], center);
      local_ants[i] = local_ants[i] + f1 + f2;
    }
    
    local_sum = sum(local_ants, num_local_ants);
    total_sum = 0; 
  
    MPI_Allreduce(&local_sum, &total_sum ,  1 , MPI_FLOAT , MPI_SUM , MPI_COMM_WORLD);

    center = total_sum / (float)(num_ants);

    if (rank == 0) {
      printf("Iteration: %d - Average position: %f\n", iter, center);
    }
  }

  // Free memory
   if (rank == 0) {
    free(ants);
  }
  free(food_source);  
  free(local_ants);

  MPI_Barrier(MPI_COMM_WORLD);
  MPI_Finalize();
  return 0;
}

# Evaluation lab - Akka

## Group number: 23

## Group members

- Martino Piaggi
- Luca Lain
- Alessandro Mosconi

## Description of message flows

For each sensors the SensorDataProcessor dispatches a ConfigMsg which contains the reference of the dispatcher.

SensorDataProcessor -> ConfigMsg (Ref to dispatcher) -> sensor_N 

Upon receiving ConfigMsg, each sensor now holds a reference to the dispatcher. The sensors start the streams of values to the dispatcher, which collect the values (by default) and assigns them to the processors with the Load Balancing logic. 

sensor_N -> TemperatureMsg -> dispatcher -> processor_M

If a the dispatcher receives (from the SensorDataProcessor) a DispatchLogicMsg it changes the logic policy based on DispatchLogicMsg value. 
In this exercitation, the SensorDataProcessor sent DispatchLogicMsg and change policy to RoundRobin logic.

SensorDataProcessor -> DispatchLogicMsg -> dispatcher

The dispatcher following the logic dispatches the TemperatureMsg to the processors which receive it.Each processor compute the average of the incoming temperatures.

sensor_N -> TemperatureMsg -> dispatcher -> processor_M

A faulty process is added (negative temperature). The negative temperature arises an exception in the processor_M. 

sensor_N -> TemperatureMsg -> dispatcher -> processor_M 

dispatcher <- Exception <- processor_M 

The dispatcher follows its specified fault tolerance strategy resuming the processor_M maintaining the average temperature.
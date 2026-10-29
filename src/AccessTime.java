public record AccessTime (int stride, int setSize, double time) { }

//A record is a special kind of class in Java that is used to model immutable data. It automatically generates constructors,
// accessors, equals(), hashCode(), and toString() methods based on the fields defined in the record. In this case, the 
//AccessTime record has three fields - stride, setsize and time.

//This file supports the memory hierarchy explorer - which measures memory access times for different stride and set size 
//combinations. The AccessTime record is used to store the results of these measurements, with each instance representing
// a specific combination of stride, set size, and the corresponding access time. The record just makes life easier.

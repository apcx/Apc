cmake_minimum_required(VERSION 3.6.0)
add_library(cpu SHARED
        src/main/cpp/cpu-features.c
        src/main/cpp/jni.cpp)
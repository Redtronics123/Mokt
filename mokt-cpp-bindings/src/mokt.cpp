/*
 * MIT License
 * Copyright 2024 Nils Jäkel
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software.
 */

#include "mokt.h"

#include <iostream>

using namespace std;

void hello() {
    cout << "Hello, World!" << endl;
}

const char *get_env(char key[]) {
    const char *value = getenv(key);
    const char* env_var(value ? value : "");
    return env_var;
}

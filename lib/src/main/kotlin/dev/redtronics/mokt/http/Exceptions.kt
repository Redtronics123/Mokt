package dev.redtronics.mokt.http

class TooManyRequestsException : Exception("Slow down! You're making too many requests! You can only make 600 requests per 10 minutes.")
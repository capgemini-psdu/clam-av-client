# clam-av-client
Provides a simple bean-style interface to the ClamAV Java client. The bean can be injected using standard Spring (or other) DI.

The bean exposes one public method that will scan a file at a specified local path (which should be treated as a quarantined area).

If a virus is found then the file is automatically deleted and an error level log entry is recorded.
#! /usr/bin/env python

import shutil
import os
import os.path
import subprocess
import sys
import glob

def go():
    subprocess.call("time java -cp lib/*:NCE.jar gui.paragraph.NCEParaGui", shell=True)

def main():
    go()

if __name__ == "__main__":
    main()



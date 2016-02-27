def download_file():
    u = urllib2.urlopen(URL)
    f = open(DOWNLOADED_FNAME, 'wb')
    meta = u.info()
    file_size = int(meta.getheaders("Content-Length")[0])
    print "Downloading: %s Bytes: %s" % (DOWNLOADED_FNAME, file_size)
    file_size_dl = 0
    block_sz = 8192
    while True:
        buffer = u.read(block_sz)
        if not buffer:
            break
        file_size_dl += len(buffer)
        f.write(buffer)
    f.close()


def send_email(user, pwd, recipient, subject, body):
    import smtplib

    gmail_user = user
    gmail_pwd = pwd
    FROM = user
    TO = recipient if type(recipient) is list else [recipient]
    SUBJECT = subject
    TEXT = body

    # Prepare actual message
    message = """\From: %s\nTo: %s\nSubject: %s\n\n%s
    """ % (FROM, ", ".join(TO), SUBJECT, TEXT)
    try:
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.ehlo()
        server.starttls()
        server.login(gmail_user, gmail_pwd)
        server.sendmail(FROM, TO, message)
        server.close()
        print 'successfully sent the mail'
    except:
        print "failed to send mail"


import urllib2
import hashlib
import os
import time
import smtplib
from email.mime.text import MIMEText

CWD = os.getcwd()
URL = "http://www.boun.edu.tr/Assets/Documents/Content/Public/kampus_hayati/yemek_listesi.pdf"
ORIGINAL_FNAME = "yemek_listesi_original.pdf"
DOWNLOADED_FNAME = "yemek_listesi.pdf"
WAIT_DUR = 600
MAIL_LIST = ['cagatay.yildiz1@gmail.com, berkantkepez@gmail.com, sodaci92@gmail.com']

ORIGINAL_FILE_PATH = CWD + "/" + ORIGINAL_FNAME
DOWNLOADED_FILE_PATH = CWD + "/" + DOWNLOADED_FNAME

while(True):
    try:
        download_file()
        hash_orig = hashlib.md5(open(ORIGINAL_FILE_PATH, 'rb').read()).hexdigest()
        hash_new = hashlib.md5(open(DOWNLOADED_FILE_PATH, 'rb').read()).hexdigest()
        if hash_new == hash_orig:
            print "no change in the file"
            os.remove(DOWNLOADED_FILE_PATH)
        else:
            print "file has changed"
            os.remove(ORIGINAL_FILE_PATH)
            os.rename(DOWNLOADED_FNAME,ORIGINAL_FNAME)
            for mail_addres in MAIL_LIST:
                send_email('bouncmpe160','160grizzly63bear',mail_addres,'yemek listesi degisti','yemek listesi degisti')
    except:
        print "exception"
    finally:
        print "sleeping"
        time.sleep(WAIT_DUR)

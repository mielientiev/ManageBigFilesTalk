package com.mielientiev.bigfiles

import java.security.MessageDigest

/**
  * Created by ihor-mielientiev on 3/3/16.
  */
class Hasher(algo: String) {

  val md = MessageDigest.getInstance(algo)

  def getHash(data: Array[Byte]) = md.digest(data).map("%02x".format(_)).mkString

}

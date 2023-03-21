package com.txy822.yourmemory.model

import com.google.firebase.firestore.PropertyName

data class UserImageList(
    @PropertyName("images") val images: List<String>? = null
)

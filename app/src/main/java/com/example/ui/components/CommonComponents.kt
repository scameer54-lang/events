package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrustBadge
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSwitcherBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Navy900,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Amber500, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROLE SWITCHER (TEST ENVIRONMENT)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = when (currentRole) {
                        UserRole.CUSTOMER -> "Customer View"
                        UserRole.VENDOR -> "Vendor View"
                        UserRole.ADMIN -> "Admin Desk"
                        UserRole.OPERATIONS -> "Ops Desk"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = Amber500,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RolePill(
                    title = "Customer",
                    subtitle = "Priya S.",
                    icon = Icons.Default.Person,
                    isSelected = currentRole == UserRole.CUSTOMER,
                    onClick = { onRoleSelected(UserRole.CUSTOMER) },
                    modifier = Modifier.weight(1f)
                )

                RolePill(
                    title = "Vendor",
                    subtitle = "Dream Decor",
                    icon = Icons.Default.Storefront,
                    isSelected = currentRole == UserRole.VENDOR,
                    onClick = { onRoleSelected(UserRole.VENDOR) },
                    modifier = Modifier.weight(1f)
                )

                RolePill(
                    title = "Admin",
                    subtitle = "Verification",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = currentRole == UserRole.ADMIN,
                    onClick = { onRoleSelected(UserRole.ADMIN) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RolePill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Indigo600 else Navy800,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Navy700),
        modifier = modifier
            .height(48.dp)
            .testTag("role_pill_${title.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else Slate400,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = if (isSelected) Indigo100 else Slate400,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TrustBadgeRow(
    isVerified: Boolean,
    isTopRated: Boolean,
    isCustomerRecommended: Boolean,
    modifier: Modifier = Modifier,
    onBadgeClick: (TrustBadge) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isVerified) {
            TrustBadgeChip(
                badge = TrustBadge.VERIFIED,
                icon = Icons.Default.Verified,
                containerColor = Emerald100,
                contentColor = Emerald600,
                onClick = { onBadgeClick(TrustBadge.VERIFIED) }
            )
        }
        if (isTopRated) {
            TrustBadgeChip(
                badge = TrustBadge.TOP_RATED,
                icon = Icons.Default.Star,
                containerColor = Amber100,
                contentColor = Amber600,
                onClick = { onBadgeClick(TrustBadge.TOP_RATED) }
            )
        }
        if (isCustomerRecommended) {
            TrustBadgeChip(
                badge = TrustBadge.CUSTOMER_RECOMMENDED,
                icon = Icons.Default.ThumbUp,
                containerColor = Indigo100,
                contentColor = Indigo600,
                onClick = { onBadgeClick(TrustBadge.CUSTOMER_RECOMMENDED) }
            )
        }
    }
}

@Composable
fun TrustBadgeChip(
    badge: TrustBadge,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.testTag("trust_badge_${badge.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = badge.title,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = when (badge) {
                    TrustBadge.VERIFIED -> "Verified"
                    TrustBadge.TOP_RATED -> "Top Rated"
                    TrustBadge.CUSTOMER_RECOMMENDED -> "Recommended"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun TrustCriteriaDialog(
    badge: TrustBadge,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            val (icon, color) = when (badge) {
                TrustBadge.VERIFIED -> Icons.Default.Verified to Emerald600
                TrustBadge.TOP_RATED -> Icons.Default.Star to Amber500
                TrustBadge.CUSTOMER_RECOMMENDED -> Icons.Default.ThumbUp to Indigo600
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                text = badge.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Qualification Criteria:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate600,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate900,
                    lineHeight = 20.sp
                )

                HorizontalDivider(color = Slate200)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Audited & backed by EventBid Procurement Team",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Got It")
            }
        }
    )
}

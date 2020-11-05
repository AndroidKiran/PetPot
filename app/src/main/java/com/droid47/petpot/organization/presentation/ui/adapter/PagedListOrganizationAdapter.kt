package com.droid47.petpot.organization.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.droid47.petpot.R
import com.droid47.petpot.base.extensions.getDimen
import com.droid47.petpot.base.extensions.openDialer
import com.droid47.petpot.base.extensions.openUrlInBrowser
import com.droid47.petpot.base.extensions.sendEmail
import com.droid47.petpot.base.widgets.BaseCheckableEntity
import com.droid47.petpot.base.widgets.BaseViewHolder
import com.droid47.petpot.databinding.ItemOrganisationBinding
import com.droid47.petpot.organization.data.models.OrganizationCheckableEntity

class PagedListOrganizationAdapter(private val onItemClick: (baseCheckableEntity: BaseCheckableEntity) -> Unit) :
    PagedListAdapter<OrganizationCheckableEntity, BaseViewHolder<OrganizationCheckableEntity>>(
        OrganizationDiffUtil
    ) {

    private object OrganizationDiffUtil : DiffUtil.ItemCallback<OrganizationCheckableEntity>() {
        override fun areItemsTheSame(
            oldItem: OrganizationCheckableEntity,
            newItem: OrganizationCheckableEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrganizationCheckableEntity,
            newItem: OrganizationCheckableEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<OrganizationCheckableEntity> {
        return OrganizationViewHolder(
            ItemOrganisationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<OrganizationCheckableEntity>,
        position: Int
    ) {
        holder.onBind(getItem(position) ?: return)
    }

    inner class OrganizationViewHolder(private val itemBinding: ItemOrganisationBinding) :
        BaseViewHolder<OrganizationCheckableEntity>(itemBinding.root) {

        override fun onBind(item: OrganizationCheckableEntity) {
            itemBinding.apply {
                organizationEntity = item
                executePendingBindings()
            }

            with(itemBinding) {
                checkboxSelected.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!itemBinding.checkboxSelected.isPressed) return@setOnCheckedChangeListener
                    onItemClick.invoke(item.apply { selected = isChecked })
                }

                btnPhone.setOnClickListener {
                    val phoneNum = item.phone ?: return@setOnClickListener
                    it.context?.openDialer(phoneNum)
                }

                btnEmail.setOnClickListener {
                    val email = item.email ?: return@setOnClickListener
                    it.context?.sendEmail(arrayOf(email))
                }

                btnWebsite.setOnClickListener {
                    val website = item.website ?: return@setOnClickListener
                    it.context?.openUrlInBrowser(website)
                }
            }
        }

        fun showItems() {
            with(itemBinding) {
                val context = this.root.context
                cvOrganization.elevation = context.getDimen(R.dimen.plane_16).toFloat()
            }
        }

        fun hideItems() {
            with(itemBinding) {
                val context = this.root.context
                cvOrganization.elevation = context.getDimen(R.dimen.plane_02).toFloat()
            }
        }

    }

    sealed class ActionType {
        class PhoneAction(val phoneNumber: String) : ActionType()
        class MailAction(val email: String) : ActionType()
        class WebsiteAction(val website: String) : ActionType()
        class ItemSelectionAction(val organizationEntity: OrganizationCheckableEntity) :
            ActionType()
    }
}
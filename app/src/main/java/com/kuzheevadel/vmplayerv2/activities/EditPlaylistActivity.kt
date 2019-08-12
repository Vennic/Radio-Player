package com.kuzheevadel.vmplayerv2.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.EditPlaylistAdapter
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.EditPlaylistViewModel
import kotlinx.android.synthetic.main.edit_playlist_layout.*
import javax.inject.Inject

class EditPlaylistActivity: AppCompatActivity() {

    @Inject
    lateinit var mAdapter: EditPlaylistAdapter

    @Inject
    lateinit var factory: CustomViewModelFactory

    private lateinit var viewModel: EditPlaylistViewModel

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Constants.themeId)
        super.onCreate(savedInstanceState)
        (application as App).getComponent().inject(this)
        mAdapter.activity = this
        setContentView(R.layout.edit_playlist_layout)
        setSupportActionBar(edit_playlist_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        edit_playlist_recycler.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@EditPlaylistActivity)
        }

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

            override fun onMove(reciclerView: RecyclerView, draged: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val adapter = reciclerView.adapter as EditPlaylistAdapter
                val draggedPosition = draged.adapterPosition
                val targetPosition = target.adapterPosition

                Log.i("MOVETEST", "draggedPosition: $draggedPosition, targetPosition: $targetPosition")
                adapter.moveItems(draggedPosition, targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                Log.i("MOVETEST", "onSwipe position: ${viewHolder.adapterPosition}")
                mAdapter.removeItem(viewHolder.adapterPosition)
            }

        })

        itemTouchHelper.attachToRecyclerView(edit_playlist_recycler)

        viewModel = ViewModelProviders.of(this, factory).get(EditPlaylistViewModel::class.java)

        viewModel.run {
            trackData.observe(this@EditPlaylistActivity, Observer {
                mAdapter.trackList = it!!
                mAdapter.notifyDataSetChanged()
            })

            loadStatus.observe(this@EditPlaylistActivity, Observer {
                if (it == State.ERROR) {
                    Toast.makeText(this@EditPlaylistActivity, "asd", Toast.LENGTH_SHORT).show()
                }
            })
        }

        viewModel.loadPlaylistFromDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.done_edit_playlist -> finishEditAndSaveChanges()

            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    fun startSwiping(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startSwipe(viewHolder)
    }

    private fun finishEditAndSaveChanges() {
        viewModel.trackList = mAdapter.trackList
        viewModel.overwriteDatabase()
        finish()
    }

}
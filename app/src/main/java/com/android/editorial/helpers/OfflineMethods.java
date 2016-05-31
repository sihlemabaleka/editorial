package com.android.editorial.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.android.editorial.data.Comment;
import com.android.editorial.data.Editorial;
import com.android.editorial.data.Interaction;
import com.android.editorial.data.Piece;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class OfflineMethods {

	Activity activity;

	public OfflineMethods(Activity activity) {
		super();
		this.activity = activity;
	}

	public Point getScreenWidth() {
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		display.getSize(point);
		return point;
	}

	public boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Boolean hasConnectivity() {
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if ((ni != null) && (ni.isConnected()))
			if (isOnline())
				return true;
			else
				return false;
		else
			return false;
	}

	public byte[] getScaledPhoto(Bitmap bitmap, int height, int width) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		float ratiobitmap = (float) width / (float) height;

		int finalWidth = 640;
		int finalheight = 640;

		if (ratiobitmap > 1) {
			finalWidth = (int) ((float) finalWidth * ratiobitmap);
		} else if (ratiobitmap < 1) {
			finalheight = (int) ((float) finalheight / ratiobitmap);
		} else {
			finalWidth = 640;
			finalheight = 640;
		}

		Bitmap bMap = Bitmap.createScaledBitmap(bitmap, finalWidth,
				finalheight, true);
		bMap.compress(CompressFormat.PNG, 100, bos);
		byte[] data = bos.toByteArray();

		if ((data.length > 0) && (data.length != 0)) {
			return data;
		}

		return null;
	}

	public byte[] getThumbnailImage(Bitmap bitmap, int height, int width) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		Bitmap scaledBitmap;
		if ((height != width) && (width > height))
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800 / 4, 480 / 4,
					false);
		else if ((height != width) && (width < height))
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, 480 / 4, 800 / 4,
					false);
		else
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

		scaledBitmap.compress(CompressFormat.JPEG, 100, bos);
		byte[] data = bos.toByteArray();

		if ((data.length > 0) && (data.length != 0)) {
			return data;
		}

		return null;
	}

	public File savedToHomeDirectory(Bitmap bitmap, int height, int width) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/tailored_posts");
		if (!myDir.exists())
			myDir.mkdir();
		UUID id = new UUID(28, 12);
		File file = new File(myDir, "Thumbnail-" + id + ".jpg");
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			Bitmap scaledBitmap;
			if ((height != width) && (width > height)) {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800 / 4,
						480 / 4, false);
			} else if ((height != width) && (width < height)) {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, 480 / 4,
						800 / 4, false);
			} else {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120,
						false);
			}

			scaledBitmap.compress(CompressFormat.JPEG, 100, out);

			out.flush();
			out.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ParseFile saveFile(byte[] data) {
		ParseFile file = new ParseFile(data);
		try {
			file.save();
			return file;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ParseFile saveThumbnail(byte[] data) {
		ParseFile file = new ParseFile(data);
		try {
			file.save();
			return file;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<ParseUser> getSearchedUsers() {
		ParseQuery<ParseUser> users = ParseUser.getQuery();
		users.fromPin(AppConstant.SEARCHED_USERS);
		try {
			return users.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Piece> getPieces(String objectId) {
		ParseQuery<Piece> pieces = ParseQuery.getQuery(Piece.class);
		pieces.fromPin(objectId);
		try {
			return pieces.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void deleteEditoria(final Editorial editorial, final ParseUser user) {
		ParseQuery<Piece> pieces = ParseQuery.getQuery(Piece.class);
		pieces.whereEqualTo("editorials", editorial);
		pieces.findInBackground(new FindCallback<Piece>() {

			@Override
			public void done(List<Piece> list, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					for (Piece piece : list) {
						piece.deleteInBackground();
						user.increment("editorial_count", -1);
						user.saveInBackground();
					}
					editorial.deleteInBackground();
				}
			}
		});
	}

	public Editorial createEditorial(String mCategory, String name, String mUrl) {
		try {

			Editorial editorial = new Editorial();
			editorial.setCategory(mCategory);
			editorial.setEditorialName(name);
			editorial.setCreatedBy(ParseUser.getCurrentUser());
			editorial.setEditorialAddress(mUrl);
			editorial.setEditorialCount();
			editorial.save();
			ParseUser user = ParseUser.getCurrentUser();
			user.increment("editorial_count");
			user.saveEventually();
			return editorial;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public Piece setEditorialPicture(String string, Editorial editorial,
			ParseFile file, String caption) {
		try {

			Piece item = new Piece();
			item.setCategory(string);
			item.setCreatedBy(ParseUser.getCurrentUser());
			item.setEditorial(editorial);
			item.setEditorialImage(file);
			item.setPieceCaption(caption);
			item.save();
			return item;
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setEditorialCoverPhoto(ParseFile file, Editorial editorial) {
		editorial.put("cover_photo", file);
		editorial.saveInBackground();
	}

	public List<Editorial> getEditorials(ParseUser user) {
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		query.whereEqualTo("created_by", user);
		query.addDescendingOrder("createdAt");
		try {
			List<Editorial> editorials = query.find();
			return editorials;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Editorial> getMyEditorials(ParseUser user) {
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		query.whereEqualTo("created_by", user);
		query.addDescendingOrder("createdAt");
		query.fromLocalDatastore();
		try {
			List<Editorial> editorials = query.find();
			return editorials;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Editorial getEditorial(String objectId) {
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		try {
			Editorial editorial = query.get(objectId);
			return editorial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Editorial getEditorialLocalDataStore(String objectId) {
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		query.fromLocalDatastore();
		try {
			Editorial editorial = query.get(objectId);
			return editorial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Piece> getEditorialPictures(String objectID) {
		ParseQuery<Piece> query = ParseQuery.getQuery(Piece.class);
		query.whereEqualTo("editorials", getEditorial(objectID));
		query.addDescendingOrder("updatedAt");
		try {
			List<Piece> pieces = query.find();
			return pieces;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<Piece> getEditorialPictures(Editorial editorial) {
		ParseQuery<Piece> query = ParseQuery.getQuery(Piece.class);
		query.whereEqualTo("editorials", editorial);
		query.addDescendingOrder("updatedAt");
		try {
			List<Piece> pieces = query.find();
			return pieces;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Piece getEditorialCoverPhoto(Editorial editorial) {
		ParseQuery<Piece> query = ParseQuery.getQuery(Piece.class);
		query.whereEqualTo("editorials", editorial);
		try {
			Piece piece = query.getFirst();
			return piece;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int followerCount(ParseUser user) {
		try {
			ParseQuery<Interaction> query = ParseQuery
					.getQuery(Interaction.class);
			query.whereEqualTo("toUser", user);
			query.whereEqualTo("type", "follow");
			query.whereNotEqualTo("fromUser", user);
			int count = query.count();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int editorialCount(ParseUser user) {
		try {
			ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
			query.whereEqualTo("user", user);
			int count = query.count();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public boolean isFollowing(ParseUser toUser, ParseUser fromUser) {

		try {
			ParseQuery<Interaction> query = ParseQuery
					.getQuery(Interaction.class);
			query.whereEqualTo("toUser", toUser);
			query.whereEqualTo("fromUser", fromUser);
			query.whereEqualTo("type", "follow");
			int count = query.count();
			if (count > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean FollowUser(ParseUser toUser, ParseUser fromUser) {

		Interaction interaction = new Interaction();
		interaction.setFromUser(fromUser);
		interaction.setToUser(toUser);
		interaction.setType("follow");
		try {
			interaction.save();
			fromUser.increment("friendCount");

			ParseRelation<ParseUser> relation = fromUser.getRelation("friends");
			relation.add(toUser);
			fromUser.saveEventually();
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			if (e.getCode() == ParseException.TIMEOUT) {
				interaction.saveEventually();
				return true;
			}
		}
		return false;
	}

	public boolean unFollowUser(final ParseUser toUser, final ParseUser fromUser) {
		ParseQuery<Interaction> query = ParseQuery.getQuery(Interaction.class);
		query.whereEqualTo("fromUser", fromUser);
		query.whereEqualTo("toUser", toUser);
		query.whereEqualTo("type", "follow");
		query.getFirstInBackground(new GetCallback<Interaction>() {

			@Override
			public void done(final Interaction item, ParseException e) {
				// TODO Auto-generated method stub
				item.deleteInBackground(new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						if (e == null) {
							fromUser.increment("friendCount", -1);
							ParseRelation<ParseObject> relation = fromUser
									.getRelation("friends");
							relation.remove(toUser);
							fromUser.saveEventually();
						} else {
							item.deleteEventually(new DeleteCallback() {

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if (e == null) {
										ParseRelation<ParseUser> relation = fromUser
												.getRelation("friends");
										relation.remove(toUser);
										fromUser.saveEventually();
									}
								}
							});
						}
					}
				});
			}
		});
		return true;
	}

	public boolean isLiked(ParseUser fromUser, Editorial post) {
		try {
			ParseQuery<Interaction> query = ParseQuery
					.getQuery(Interaction.class);
			query.whereEqualTo("fromUser", fromUser);
			query.whereEqualTo("editorial_image", post);
			int count = query.count();
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean likeEditorial(final ParseUser fromUser, final Editorial post) {

		final Interaction activity = new Interaction();
		activity.setFromUser(fromUser);
		activity.setEditorial(post);
		activity.setType("like");
		try {
			activity.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						post.increment("likeCount");

						ParseRelation<ParseObject> postRelation = post
								.getRelation("likers");
						postRelation.add(fromUser);

						post.saveEventually();

						ParseRelation<ParseObject> relation = fromUser
								.getRelation("likes");
						relation.add(post);
						fromUser.saveEventually();
					} else {
						activity.saveEventually(new SaveCallback() {

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if (e == null) {
									post.increment("likeCount");

									ParseRelation<ParseObject> postRelation = post
											.getRelation("likers");
									postRelation.add(fromUser);
									post.saveEventually();

									ParseRelation<ParseObject> relation = fromUser
											.getRelation("likes");
									relation.add(post);
									fromUser.saveEventually();
								}
							}
						});
					}
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unlikePost(final ParseUser fromUser, final Editorial post) {
		try {
			ParseQuery<Interaction> query = ParseQuery
					.getQuery(Interaction.class);
			query.whereEqualTo("fromUser", fromUser);
			query.whereEqualTo("editorial_image", post);
			query.whereEqualTo("type", "like");
			query.getFirstInBackground(new GetCallback<Interaction>() {

				@Override
				public void done(final Interaction activity, ParseException e) {
					// TODO Auto-generated method stub
					if (e != null)
						if (activity == null) {
							return;
						}
					activity.deleteInBackground(new DeleteCallback() {

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (e == null) {

								post.increment("likeCount", -1);

								ParseRelation<ParseObject> relation = post
										.getRelation("likers");
								relation.remove(fromUser);
								post.saveEventually();

								ParseRelation<ParseObject> userRelation = fromUser
										.getRelation("likes");
								userRelation.remove(post);

								fromUser.saveEventually();

							} else {
								activity.deleteEventually(new DeleteCallback() {

									@Override
									public void done(ParseException e) {
										// TODO Auto-generated method stub
										if (e == null) {
											post.increment("likes", -1);

											ParseRelation<ParseObject> relation = post
													.getRelation("likers");
											relation.remove(fromUser);
											post.saveEventually();

											ParseRelation<ParseObject> userRelation = fromUser
													.getRelation("likes");
											userRelation.remove(post);

											fromUser.saveEventually();
										}
									}
								});
							}
						}
					});
				}
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void postComment(final ParseUser fromUser, final Editorial post,
			String content) {

		final Comment comment = new Comment();
		comment.setCreatedBy(fromUser);
		comment.setComment(content);
		comment.setEditorial(post);

		final Interaction activity = new Interaction();
		activity.setEditorial(post);
		activity.setFromUser(fromUser);
		activity.setType("comment");
		try {
			comment.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						activity.saveInBackground();
						post.increment("commentCount");
						post.saveEventually();
					} else {
						comment.saveEventually(new SaveCallback() {

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if (e == null) {
									activity.saveInBackground();
									post.increment("commentCount");
									post.saveEventually();
								}
							}
						});
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
